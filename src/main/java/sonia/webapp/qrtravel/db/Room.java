/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author th
 */
@Entity(name = "room")
@ToString
public class Room implements Serializable
{
  public Room()
  {
  }

  public Room(String pin)
  {
    this.pin = pin;
  }

  public int getAttendeesCount()
  {
    int count = 0;
    
    if ( attendees != null )
    {
      count = attendees.size();
    }
    
    return count;
  }
  
  @Id
  @Getter
  private String pin;

  @Getter
  private String description;

  @Getter
  @Column(name = "owner_uid")
  private String ownerUid;

  @Getter
  private String domain;

  @Getter
  private String creation;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rtype", nullable = false)
  @Getter
  private RoomType roomType;

  @OneToMany(
    mappedBy = "pin",
    fetch = FetchType.EAGER
  )
  @Getter
  private List<Attendee> attendees;
}
