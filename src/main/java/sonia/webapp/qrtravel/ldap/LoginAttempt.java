package sonia.webapp.qrtravel.ldap;

import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@ToString
public class LoginAttempt
{
  public LoginAttempt(String username)
  {
    this.username = username;
    this.creationTimestamp = System.currentTimeMillis();
    this.counter = 0;
  }

  public int incrementCounter()
  {
    return ++counter;
  }

  @Getter
  private String username;

  @Getter
  private long creationTimestamp;

  @Getter
  private int counter;
}
