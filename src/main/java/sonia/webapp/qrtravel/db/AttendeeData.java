package sonia.webapp.qrtravel.db;

import lombok.Getter;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public interface AttendeeData
{
   public String getUuid();
   public String getMail();
   public String getSurname();
   public String getGivenName();
   public String getLocation();
   public String getPhone();
   public String getStreet();
   public String getCity();
}
