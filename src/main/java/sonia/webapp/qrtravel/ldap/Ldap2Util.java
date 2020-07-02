/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.ldap;

import com.google.common.base.Strings;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import java.text.MessageFormat;
import java.util.List;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.Config;

/**
 *
 * @author th
 */
public class Ldap2Util
{
  private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Ldap2Util.class.getName());

  private final static Config CONFIG = Config.getInstance();

  public static Ldap2Account bind(String uid, String password)
  {
    Ldap2Account account = null;
    LDAPConnection connection = null;

    try
    {
      connection = Ldap2ConnectionFactory.getConnection();
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
            account = new Ldap2Account(connection.getEntry(dn, "uid", "mail",
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

  public static Ldap2Account searchForMail(String mail)
  {
    Ldap2Account account = null;
    LDAPConnection connection = null;

    LOGGER.debug("LDAP search for mail = " + mail);

    try
    {
      connection = Ldap2ConnectionFactory.getConnection();

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
        account = new Ldap2Account(searchResultEntry.get(0));
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
}
