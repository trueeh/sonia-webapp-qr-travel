package sonia.webapp.qrtravel.address;

import lombok.Getter;

/**
 *
 * @author Dr.-Ing. Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class Address
{
  @Getter
  private String description;
  
  @Getter
  private String phoneNumber;

  @Getter
  private String street;
  
  @Getter
  private String city;
}
