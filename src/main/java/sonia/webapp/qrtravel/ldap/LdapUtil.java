/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.ldap;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.Config;

/**
 *
 * @author th
 */
public class LdapUtil
{
  private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    LdapUtil.class.getName());

  private final static Config CONFIG = Config.getInstance();

  private final static LdapUtil SINGLETON = new LdapUtil();

  private LdapUtil()
  {
    loginAttemptsCache = CacheBuilder.newBuilder().expireAfterWrite(CONFIG.
      getLoginFailedBlockingDuration(), TimeUnit.SECONDS).
      build(new CacheLoader<String, LoginAttempt>()
      {
        @Override
        public LoginAttempt load(String username)
        {
          LOGGER.debug("load login attempt cache for username=" + username);
          return new LoginAttempt(username);
        }
      });
  }

  public static LdapAccount bind(String uid, String password)
  {
    LdapAccount account = null;
    LDAPConnection connection = null;

    try
    {
      connection = LdapConnectionFactory.getConnection();
      SearchResult searchResult = connection.search(CONFIG.getLdapBaseDn(),
        getSearchScope(), "(uid=" + uid + ")",
        "uid");

      String dn = null;

      if (searchResult.getEntryCount() == 1) // uid is unique
      {
        dn = searchResult.getSearchEntries().get(0).getDN();
      }

      LOGGER.debug("dn=" + dn);

      if (!Strings.isNullOrEmpty(dn))
      {
        try
        {
          BindResult bindResult = connection.bind(dn, password);
          if (bindResult.getResultCode() == ResultCode.SUCCESS)
          {
            LOGGER.debug("dn=" + dn + " bind success");
            account = new LdapAccount(connection.getEntry(dn, "uid", "mail",
              "sn",
              "givenName", "soniaStudentNumber"));
            LOGGER.debug(account.toString());
          }
        }
        catch (LDAPException ex)
        {
          LOGGER.debug("dn=" + dn + " bind failed");
        }
      }
    }
    catch (LDAPException ex)
    {
      LOGGER.error("searchForMail", ex);
    }
    finally
    {
      if (connection != null)
      {
        connection.close();
      }
    }

    return account;
  }

  public static LdapAccount searchForMail(String mail)
  {
    LdapAccount account = null;
    LDAPConnection connection = null;

    LOGGER.debug("LDAP search for mail = " + mail);

    try
    {
      connection = LdapConnectionFactory.getConnection();

      MessageFormat searchFormat = new MessageFormat(CONFIG.
        getLdapSearchFilter());

      String searchFilter = searchFormat.format(new Object[]
      {
        mail
      });

      SearchResult searchResult = connection.search(CONFIG.getLdapBaseDn(),
        getSearchScope(), searchFilter,
        "uid", "mail", "sn", "givenName", "soniaStudentNumber");

      List<SearchResultEntry> searchResultEntry = searchResult.
        getSearchEntries();

      if (searchResultEntry.size() > 0)
      {
        account = new LdapAccount(searchResultEntry.get(0));
        LOGGER.debug(account.toString());
      }
    }
    catch (LDAPException ex)
    {
      LOGGER.error("searchForMail", ex);
    }
    finally
    {
      if (connection != null)
      {
        connection.close();
      }
    }

    return account;
  }

  private static SearchScope getSearchScope()
  {
    SearchScope scope;

    switch (CONFIG.getLdapSearchScope())
    {
      case "ONE":
        scope = SearchScope.ONE;
        break;

      case "SUB":
        scope = SearchScope.SUB;
        break;

      default:
        scope = SearchScope.BASE;
    }

    return scope;
  }

  private synchronized LoginAttempt _getLoginAttempt(String username)
  {
    LOGGER.debug("_getLoginAttempt");
    LoginAttempt loginAttempt = null;

    if (!Strings.isNullOrEmpty(username))
    {
      try
      {
        loginAttempt = loginAttemptsCache.get(username);
        LOGGER.debug(loginAttempt.toString());
      }
      catch (ExecutionException ex)
      {
        LOGGER.error("Failed to get login attempt from cache ", ex);
      }
    }
    return loginAttempt;
  }

  public static LoginAttempt getLoginAttempt(String username)
  {
    return SINGLETON._getLoginAttempt(username);
  }

  private LoadingCache<String, LoginAttempt> loginAttemptsCache;
}
