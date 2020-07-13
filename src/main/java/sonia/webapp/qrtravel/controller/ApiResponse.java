package sonia.webapp.qrtravel.controller;

import lombok.Getter;

/**
 *
 * @author th
 */
public class ApiResponse
{
  public final static int OK = 0;
  public final static int ERROR = 1;
  public final static int INVALID_CREDENTIALS = 2;
  public final static int UNKNOWN_ROOM = 3;
  public final static int ACCOUNT_BLOCKED = 4;
  public final static int PHONENUMBER_IS_MISSING = 5;

  public ApiResponse( int code, String message )
  {
    this.code = code;
    this.message = message;
  }
  
  @Getter
  private int code;
  
  @Getter
  private String message;
}
