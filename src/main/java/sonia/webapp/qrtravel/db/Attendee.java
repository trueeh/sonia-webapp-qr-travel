/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.db;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author th
 */
@Entity(name = "attendee")
@ToString
public class Attendee implements Serializable
{
  @Getter
  @Id
  private long id;
  
  @Getter
  private String pin;
  
  @Getter
  private String arrive;
  
  @Getter
  private String email;
  
  @Getter
  private String phonenumber;
  
  @Getter
  private String surname;
  
  @Getter
  private String givenname;
  
  @Getter
  private String studentnumber;
  
  @Getter
  private String departure;
  
  @Getter
  private String location;
  
  @Getter
  @Column(name = "cookieUUID")
  private String cookieUUID;
}
