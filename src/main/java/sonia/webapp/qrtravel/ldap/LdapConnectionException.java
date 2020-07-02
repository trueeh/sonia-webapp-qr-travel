/**
 * OSTFALIA, COMPUTING CENTER CONFIDENTIAL
 *
 * 2000 - 2013 Ostfalia University of Applied Sciences, Computing Center
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Ostfalia University of Applied Sciences, Computing Center and its suppliers.
 * The intellectual and technical concepts contained herein are proprietary to
 * Ostfalia University of Applied Sciences, Computing Center. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from Ostfalia University of Applied Sciences,
 * Computing Center.
 */


package sonia.webapp.qrtravel.ldap;

/**
 *
 * @author Sebastian Sdorra
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
