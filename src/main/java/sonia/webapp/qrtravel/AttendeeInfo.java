/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author th
 */
public class AttendeeInfo
{
  @Getter
  @Setter
  private String mail;
  
  @Getter
  @Setter
  private String surname;
  
  @Getter
  @Setter
  private String givenname;
  
  @Getter
  @Setter
  private String studentnumber;
}
