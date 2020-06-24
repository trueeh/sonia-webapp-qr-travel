/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.Config;

/**
 *
 * @author th
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
    LOGGER.info("Initialize Database");
    for (RoomType rt : listRoomTypes())
    {
      System.out.println(rt);
    }

    Room room = findRoom("123456");

    System.out.println(room);
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

  public static void persist( Attendee attendee )
  { 
    LOGGER.info( "persist attendee=" + attendee.toString());

    try (Session session = getEntityManager().unwrap(Session.class))
    {
      Transaction transaction = session.beginTransaction();
      if ( attendee.getId() == 0)
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
  
  public static Attendee merge( Attendee attendee )
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
      TypedQuery<Attendee> query = getEntityManager().createNamedQuery( "lastAttendeeEntry", Attendee.class);

      
      query.setParameter("pin", pin);
      query.setParameter("uuid", uuid);

      try
      {
        resultList = query.getResultList();
     
        if ( resultList != null && resultList.size() > 0 )
        {
          lastAttendee = resultList.get(0);
        }
      }
      catch (javax.persistence.NoResultException e)
      {
        LOGGER.trace("findByName: ", e);
      }

      System.out.println(lastAttendee);
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

  private transient LoadingCache<String, Room> roomCache;

  private transient final EntityManagerFactory entityManagerFactory;
}
