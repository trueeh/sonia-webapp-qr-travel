package sonia.webapp.qrtravel.db;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
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
      LOGGER.debug("list", e);
    }

    return result;
  }

  public static void deleteExpiredAttendeeEntries(long expirationTimestamp)
  {
    int deletedEntries = 0;
    LOGGER.debug("deleteExpiredAttendeeEntries - expirationTimestamp={}",
      expirationTimestamp);

    CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();

    CriteriaDelete<Attendee> criteriaDelete = criteriaBuilder.
      createCriteriaDelete(
        Attendee.class);

    Root<Attendee> entry = criteriaDelete.from(Attendee.class);

    criteriaDelete.where(criteriaBuilder.lessThan(entry.get("updatedTimestamp"),
      expirationTimestamp));

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

  private transient LoadingCache<String, Room> roomCache;

  private transient final EntityManagerFactory entityManagerFactory;
}
