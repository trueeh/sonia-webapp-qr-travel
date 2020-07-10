package sonia.webapp.qrtravel.ldap;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>Sebastian Sdorra
 */
public class LdapConnectionException extends RuntimeException
{

  /** Field description */
  private static final long serialVersionUID = 1916708941089543928L;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public LdapConnectionException() {}

  /**
   * Constructs ...
   *
   *
   * @param message
   */
  public LdapConnectionException(String message)
  {
    super(message);
  }

  /**
   * Constructs ...
   *
   *
   * @param cause
   */
  public LdapConnectionException(Throwable cause)
  {
    super(cause);
  }

  /**
   * Constructs ...
   *
   *
   * @param message
   * @param cause
   */
  public LdapConnectionException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
