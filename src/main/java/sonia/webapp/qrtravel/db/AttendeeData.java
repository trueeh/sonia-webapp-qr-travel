/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.db;

import lombok.Getter;

/**
 *
 * @author th
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
