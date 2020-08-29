package sonia.webapp.qrtravel.api;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class ApiCardResponse
{

  public ApiCardResponse( int code, String message )
  {
    this.code = code;
    this.message = message;
  }
  
  @Getter
  private final int code;
  
  @Getter
  private final String message;
  
  @Getter
  @Setter
  private String mail;

  @Getter
  @Setter
  private String uid;

  @Getter
  @Setter
  private String sn;

  @Getter
  @Setter
  private String givenName;

  @Getter
  @Setter
  private String soniaStudentNumber;

  @Getter
  @Setter
  private String soniaChipcardBarcode;
  
  @Getter
  @Setter
  private String ou;
  
  @Getter
  @Setter
  private String employeeType;
  
  @Getter
  @Setter
  private String phoneNumber;
  
  @Getter
  @Setter
  private String jpegPhoto;  
}
