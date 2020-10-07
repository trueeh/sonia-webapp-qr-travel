package sonia.webapp.qrtravel.util;

import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Dr.-Ing. Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@ToString
public class ErrorMessage
{
  
  public ErrorMessage()
  {
    this.title = "";
    this.message = "";
    this.processed = true;
  }
  
  public ErrorMessage( String title, String message )
  {
    this.title = title;
    this.message = message;
    this.processed = false;
  }
  
  public boolean isDone()
  {
    boolean value = processed;
    processed = true;
    return value;
  }

  @Getter
  private boolean processed;
  
  @Getter
  private final String title;
  
  @Getter
  private final String message;
}
