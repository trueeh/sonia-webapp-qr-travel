package sonia.webapp.qrtravel.db;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.Config;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class Database
{
  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(
    Database.class.getName());

  public final static SimpleDateFormat DATE_TIME = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss");

  /**
   * Field description
   */
  private final static String PERSISTANCE_UNIT_NAME = "qrPU";

  /**
   * Field description
   */
  private final static Database SINGLETON = new Database();

  //~--- constructors ---------------------------------------------------------
  /**
   * Constructs ...
   *
   */
  private Database()
  {
    Map<String, String> properties = new HashMap<>();

    properties.put("javax.persistence.jdbc.url", CONFIG.getDbUrl());
    properties.put("javax.persistence.jdbc.user", CONFIG.getDbUser());
    properties.put("javax.persistence.jdbc.driver", CONFIG.
      getDbDriverClassName());
    properties.put("javax.persistence.jdbc.password", CONFIG.getDbPassword());

    this.entityManagerFactory = new org.hibernate.jpa.HibernatePersistenceProvider().
      createEntityManagerFactory(
        PERSISTANCE_UNIT_NAME, properties);

    roomCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).
      build(new CacheLoader<String, Room>()
      {
        @Override
        public Room load(String pin)
        {
          LOGGER.debug("load room cache for pin=" + pin);
          return getEntityManager().find(Room.class, pin);
        }
      });
  }

  public static void initialize()
  {
    LOGGER.debug("Initialize Database");
    for (RoomType rt : listRoomTypes())
    {
      LOGGER.info(rt.toString());
    }
  }

  public static Room findRoom(String pin)
  {
    Room room = null;

    try
    {
      room = SINGLETON.roomCache.get(pin);
    }
    catch (Exception ex)
    {
      LOGGER.error("Room PIN=" + pin + " not found!");
    }

    return room;
  }

  private static EntityManager getEntityManager()
  {
    return SINGLETON.entityManagerFactory.createEntityManager();
  }

  public static void persist(Attendee attendee)
  {
    LOGGER.debug("persist attendee=" + attendee.toString());

    try (Session session = getEntityManager().unwrap(Session.class))
    {
      Transaction transaction = session.beginTransaction();
      if (attendee.getId() == 0)
      {
        session.save(attendee);
      }
      else
      {
        session.update(attendee);
      }
      transaction.commit();
    }
  }

  public static void persist(Room room)
  {
    LOGGER.debug("persist room=" + room.toString());

    try (Session session = getEntityManager().unwrap(Session.class))
    {
      Transaction transaction = session.beginTransaction();
      session.save(room);
      transaction.commit();
    }
  }

  public static Attendee merge(Attendee attendee)
  {
    Session session = getEntityManager().unwrap(Session.class);
    Transaction transaction = session.beginTransaction();
    transaction.begin();
    Attendee a = getEntityManager().merge(attendee);
    transaction.commit();
    return a;
  }

  public static Attendee lastAttendeeEntry(String pin, String uuid)
  {
    Attendee lastAttendee = null;

    if (!Strings.isNullOrEmpty(pin) && !Strings.isNullOrEmpty(uuid))
    {
      List<Attendee> resultList;
      TypedQuery<Attendee> query = getEntityManager().createNamedQuery(
        "lastAttendeeEntry", Attendee.class);

      query.setFirstResult(0);
      query.setMaxResults(1);
      query.setParameter("pin", pin);
      query.setParameter("uuid", uuid);

      try
      {
        resultList = query.getResultList();

        if (resultList != null && resultList.size() > 0)
        {
          lastAttendee = resultList.get(0);
        }
      }
      catch (javax.persistence.NoResultException e)
      {
        LOGGER.trace("findByName: ", e);
      }

      LOGGER.debug("Last attendee: " + lastAttendee);
    }

    return lastAttendee;
  }

  public static List<RoomType> listRoomTypes()
  {
    List<Object[]> resultList;
    List<RoomType> result = null;

    TypedQuery<Object[]> query = getEntityManager().createNamedQuery(
      "listRoomTypes", Object[].class);

    try
    {
      resultList = query.getResultList();
      result = new ArrayList<>();

      for (Object[] o : resultList)
      {
        result.add((RoomType) o[0]);
      }

    }
    catch (javax.persistence.NoResultException e)
    {
      LOGGER.debug("list room types", e);
    }

    return result;
  }

  public static List<Room> listRooms()
  {
    List<Object[]> resultList;
    List<Room> result = null;

    TypedQuery<Object[]> query = getEntityManager().createNamedQuery(
      "listRooms", Object[].class);

    try
    {
      resultList = query.getResultList();
      result = new ArrayList<>();

      for (Object[] o : resultList)
      {
        result.add((Room) o[0]);
      }

    }
    catch (javax.persistence.NoResultException e)
    {
      LOGGER.debug("list rooms", e);
    }

    return result;
  }

  /**
   * find roomType by id
 * @param id	id of RoomType
 * @return	RoomType
 */
public static RoomType searchRoomTypeById(int id)
  {
    return getEntityManager().find(RoomType.class, id);
  }

/**
 * search  rooms by roomtype and description (wildcard) 
 * @param roomType 	the RoomType
 * @param description	the description
 * @return list of rooms
 */
public static List<Room> searchRoomsByTypeAndDescription(RoomType roomType, String description)
  {
    List<Object[]> resultList;
    List<Room> result = null;

    TypedQuery<Object[]> query = getEntityManager().createNamedQuery(
      "searchRoomsByTypeAndDescription", Object[].class);

    try
    {
      query.setParameter("roomType", roomType );
      query.setParameter("description", "%" + description.toUpperCase() + "%");
      resultList = query.getResultList();
      result = new ArrayList<>();

      for (Object[] o : resultList)
      {
        result.add((Room) o[0]);
      }

    }
    catch (javax.persistence.NoResultException e)
    {
      LOGGER.debug("search rooms by type and description", e);
    }

    return result;
  }

/**
 * search rooms by type
 * @param roomType	the roomtype
 * @return list of rooms
 */
public static List<Room> searchRoomsByType(RoomType roomType)
  {
    List<Object[]> resultList;
    List<Room> result = null;

    TypedQuery<Object[]> query = getEntityManager().createNamedQuery(
      "searchRoomsByType", Object[].class);

    try
    {
      query.setParameter("roomType", roomType );
      resultList = query.getResultList();
      result = new ArrayList<>();

      for (Object[] o : resultList)
      {
        result.add((Room) o[0]);
      }

    }
    catch (javax.persistence.NoResultException e)
    {
      LOGGER.debug("search rooms by type", e);
    }

    return result;
  }

/**
 * search rooms by description (wildcard) 
 * @param description the description
 * @return list of rooms
 */
public static List<Room> searchRoomsByDescription(String description)
  {
    List<Object[]> resultList;
    List<Room> result = null;

    TypedQuery<Object[]> query = getEntityManager().createNamedQuery(
      "searchRoomsByDescription", Object[].class);

    try
    {
      query.setParameter("description", "%" + description.toUpperCase() + "%");
      resultList = query.getResultList();
      result = new ArrayList<>();

      for (Object[] o : resultList)
      {
        result.add((Room) o[0]);
      }

    }
    catch (javax.persistence.NoResultException e)
    {
      LOGGER.debug("search rooms by description", e);
    }

    return result;
  }

  public static void deleteExpiredAttendeeEntries(long expirationTimestamp)
  {
    int deletedEntries = 0;
    LOGGER.debug("deleteExpiredAttendeeEntries - expirationTimestamp={}",
      expirationTimestamp);

    String hql = "select a.id from attendee a, room r where a.createdTimestamp < :timestamp and a.pin = r.pin and r.roomType > 1";

    Session session = getEntityManager().unwrap(Session.class);
    Transaction transaction = session.beginTransaction();

    List<Number> ids = session.createQuery(hql).setParameter("timestamp",
      expirationTimestamp).list();

    if (ids != null && ids.size() > 0)
    {
      deletedEntries = session.createQuery(
        "delete from attendee a where a.id in (:ids)").setParameterList("ids",
          ids).executeUpdate();
    }
    transaction.commit();

    LOGGER.info("Number of deleted attendee entries = {}", deletedEntries);
  }

  public static void departureMaxDurationAttendeeEntries(
    long maxDurationTimestamp)
  {
    int departuredEntries = 0;
    LOGGER.debug("departureMaxDurationAttendeeEntries - expirationTimestamp={}",
      maxDurationTimestamp);

    String hql = "select a.id from attendee a where a.createdTimestamp < :timestamp and a.departure is null";

    Session session = getEntityManager().unwrap(Session.class);
    Transaction transaction = session.beginTransaction();

    List<Number> ids = session.createQuery(hql).setParameter("timestamp",
      maxDurationTimestamp).list();

    String departure = DATE_TIME.format(new Date());

    if (ids != null && ids.size() > 0)
    {
      departuredEntries = session.createQuery(
        "update attendee a set a.departure=:departure where a.id in (:ids)").
        setParameterList("ids",
          ids).setParameter("departure", departure).executeUpdate();
    }

    transaction.commit();

    if (departuredEntries > 0)
    {
      LOGGER.info("Number of departured attendee entries = {}",
        departuredEntries);
    }
  }

  public static void storeStatistics()
  {
    LOGGER.debug("storeStatistics");

    List<Room> roomList = listRooms();
    for (Room room : roomList)
    {
      int numberOfAttendees = 0;
      int currentAttendees = 0;
      int departuredAttendees = 0;
      int forcedDeparture = 0;
      int averageDuration = 0;
      int durationSum = 0;
      int correctDepartureAttendees = 0;

      long forcedDepartureTime = CONFIG.getMaxDurationInMinutes() * 60 * 1000;

      if (room.getAttendees() != null && room.getAttendees().size() > 0)
      {
        for (Attendee attendee : room.getAttendees())
        {
          numberOfAttendees++;

          if (Strings.isNullOrEmpty(attendee.getDeparture()))
          {
            currentAttendees++;
          }
          else
          {
            
            long durationTime = attendee.getUpdatedTimestamp() - attendee.getCreatedTimestamp();
            
            if ( attendee.getUpdatedTimestamp() == 0 || durationTime >= forcedDepartureTime)
            {
              forcedDeparture++;
            }
            else
            {
              durationSum += durationTime;
              correctDepartureAttendees++;
            }
            departuredAttendees++;
          }
        }
      }

      if ( correctDepartureAttendees > 0 )
      {
        averageDuration = ( durationSum / correctDepartureAttendees ) / ( 1000 * 60 );
      }
      
      InfluxDbWriter.write(room.getPin(), numberOfAttendees,
        currentAttendees, departuredAttendees, forcedDeparture, averageDuration);
    }
  }

  private transient LoadingCache<String, Room> roomCache;

  private transient final EntityManagerFactory entityManagerFactory;
}
