package sonia.webapp.qrtravel.form;

import sonia.webapp.qrtravel.QrTravelToken;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
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
