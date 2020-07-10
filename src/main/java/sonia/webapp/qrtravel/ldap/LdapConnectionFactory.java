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

//~--- non-JDK imports --------------------------------------------------------

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

//~--- JDK imports ------------------------------------------------------------

import java.security.GeneralSecurityException;


import javax.net.ssl.SSLSocketFactory;
import sonia.webapp.qrtravel.Config;

/**
 *
 * @author th
 */
public final class LdapConnectionFactory
{
  private final static Config CONFIG = Config.getInstance();
 
  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  private LdapConnectionFactory() {}

  //~--- methods --------------------------------------------------------------

  
  //~--- get methods ----------------------------------------------------------
 
  /**
   * Method description
   *
   *
   * @return
   *
   * @throws LDAPException
   */
  public static LDAPConnection getConnection()
    throws LDAPException
  {

    LDAPConnectionOptions options = new LDAPConnectionOptions();

    options.setAutoReconnect(true);

    LDAPConnection connection;

    if (CONFIG.isLdapHostSSL())
    {
      connection = new LDAPConnection(createSSLSocketFactory(), options,
        CONFIG.getLdapHostName(), CONFIG.getLdapHostPort(),
        CONFIG.getLdapBindDn(),
        CONFIG.getLdapBindPassword());
    }
    else
    {
      connection = new LDAPConnection(options, CONFIG.getLdapHostName(), 
        CONFIG.getLdapHostPort(),
        CONFIG.getLdapBindDn(),
        CONFIG.getLdapBindPassword());
    }

    connection.setConnectionName(CONFIG.getLdapHostName());

    return connection;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  private static SSLSocketFactory createSSLSocketFactory()
  {

    // trust all ??
    try
    {
      SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());

      return sslUtil.createSSLSocketFactory();
    }
    catch (GeneralSecurityException ex)
    {
      throw new LdapConnectionException("could not create ldap connection", ex);
    }
  }
}
