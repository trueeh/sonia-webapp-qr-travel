package sonia.webapp.qrtravel.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@Entity(name = "room")
@NamedQueries(
  {
    @NamedQuery(name = "listRooms",
                query = "select a, upper(a.description) as orderName from room a order by orderName"),
    @NamedQuery(name = "searchRoomsByTypeAndDescription",
    			query = "select a, upper(a.description) as orderName from room a where a.roomType = :roomType and upper(a.description) like :description order by orderName"),
    @NamedQuery(name = "searchRoomsByType",
    			query = "select a, upper(a.description) as orderName from room a where a.roomType = :roomType order by orderName"),
    @NamedQuery(name = "searchRoomsByDescription",
				query = "select a, upper(a.description) as orderName from room a where upper(a.description) like :description order by orderName")
  })
@ToString
public class Room implements Serializable
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    Room.class.getName());

  public Room()
  {
  }

  public Room(String pin)
  {
    this.pin = pin;
  }

  public Room(String pin, int type, String description, String ownerUid,
    String domain)
  {
    this.pin = pin;
    this.description = description;
    this.ownerUid = ownerUid;
    this.domain = domain;
    for (RoomType t : Database.listRoomTypes())
    {
      if (t.getRtype() == type)
      {
        roomType = t;
      }
    }
  }

  @PrePersist
  public void prePersist()
  {
    creation = new Date();
    LOGGER.debug("prePersist " + this.getClass().getCanonicalName() 
      + ", " + this.toString());
  }

  @Override
  public boolean equals(Object obj)
  {
    boolean same = false;

    if (this == obj)
    {
      return true;
    }

    if ((obj != null) && (obj instanceof Room))
    {
      same = this.pin.equals(((Room) obj).pin);
    }

    return same;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;

    hash = 37 * hash + Objects.hashCode(this.pin);

    return hash;
  }

  public int getAttendeesCount()
  {
    int count = 0;

    if (attendees != null)
    {
      for (Attendee a : attendees)
      {
        if (Strings.isNullOrEmpty(a.getDeparture()))
        {
          count++;
        }
      }
    }

    return count;
  }

  public String getStatistics()
  {
    return Integer.toString(attendees.size()) + "/"
      + Integer.toString(getAttendeesCount());
  }

  @Id
  @Getter
  private String pin;

  @Getter
  private String description;

  @Getter
  @Column(name = "owner_uid")
  private @JsonIgnore
  String ownerUid;

  @Getter
  @Column(unique=true, nullable=true, name = "external_id")
  private @JsonIgnore
  Long externalId;

  @Getter
  private String domain;

  @Getter
  @JsonIgnore
  @Temporal(TemporalType.TIMESTAMP)
  private Date creation;
  
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rtype", nullable = false)
  @Getter
  private RoomType roomType;

  @OneToMany(
    mappedBy = "pin",
    fetch = FetchType.EAGER
  )
  @Getter
  private @JsonIgnore
  List<Attendee> attendees;
}
