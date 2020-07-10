/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sonia.webapp.qrtravel.QrTravelToken;

/**
 *
 * @author th
 */
@ToString
public class ExamForm implements AttendeeForm
{
  
  public void setAttendeeData(String pin, QrTravelToken token)
  {
    this.pin = pin;
    this.phone = token.getPhone();
    this.location = token.getLocation();
    this.city = token.getCity();
    this.street = token.getStreet();
    this.userId = token.getUid();
    this.password = token.getPassword();
  }
  
  
  @NotNull
  @Size( min = 6, message = "Minimum 6 Zeichen" )  
  @Getter
  @Setter
  private String pin;
    
  @NotNull
  @Size( min = 4, message = "Minimum 4 Zeichen" )
  @Getter
  @Setter
  private String phone;
  
  @NotNull
  @Getter
  @Setter
  @Size( min = 2, message = "Minimum 2 Zeichen" )
  private String userId;
  
  @NotNull
  @Getter
  @Setter
  @Size( min = 6, message = "Minimum 6 Zeichen" )
  private String password;
  
  @Getter
  @Setter
  private String location;
  
  @Getter
  @Setter
  @NotNull
  @Size( min = 2, message = "Minimum 2 Zeichen" )
  private String street;

  @Getter
  @Setter
  @NotNull
  @Size( min = 2, message = "Minimum 2 Zeichen" )
  private String city;
}
