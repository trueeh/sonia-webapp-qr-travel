/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.form;

import sonia.webapp.qrtravel.QrTravelToken;

/**
 *
 * @author th
 */
public interface AttendeeForm
{
  public String getPin();
  public String getLocation();
  public String getPhone();
  public String getStreet();
  public String getCity();
  
  public void setAttendeeData(String pin, QrTravelToken token);
}
