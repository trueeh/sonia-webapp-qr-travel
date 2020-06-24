/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author th
 */
@ToString
public class AttendeeInfo
{
  @Getter
  @Setter
  private String pin;
  
  @Getter
  @Setter
  private String mail;
  
  @Getter
  @Setter
  private String phone;
  
  @Getter
  @Setter
  private String surname;
  
  @Getter
  @Setter
  private String givenName;
  
  @Getter
  @Setter
  private String studentnumber;
  
  @Getter
  @Setter
  private String location;

  @Getter
  @Setter
  private String uid;

  @Getter
  @Setter
  private String password;
}
