package sonia.nginx.ldap.auth;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import sonia.commons.ldap.LDAPConnectionFactory;

@RestController
public class AuthController
{
  public final static String AUTH_SERVICE_TOKEN = "AuthServiceToken";
  public final static String UNAUTHORIZED = "UNAUTHORIZED";
  private final static Config CONFIG = Config.getInstance();
  private final static Logger LOGGER = LoggerFactory.getLogger(
    AuthController.class.getName());

  public static void addTokenToHttpServletResponse( AuthToken token, HttpServletResponse response)
  {
    if ( token == null )
    {
      response.addCookie(AuthToken.getLogoutCookie());
    }
    else
    {
      response.addCookie(token.toCookie());
    }
    response.addHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
    response.addHeader("Pragma", "no-cache");    
  }
  
  @RequestMapping(path = "/check", produces = "text/html")
  public ResponseEntity check(
    @RequestHeader( name="x-original-uri", required = false, defaultValue = "" ) String originalUri,
    @CookieValue(value = AUTH_SERVICE_TOKEN,
                 defaultValue = UNAUTHORIZED) String tokenValue)
  {
    LOGGER.debug("ckeck");
    System.out.println("- check home url=" + CONFIG.getHomeUrl());
    System.out.println("  - x-original-uri=" + originalUri);

    AuthToken token = AuthToken.fromCookieValue(tokenValue);

    HttpStatus status = (token != null && token.isAuthenticated())
      ? HttpStatus.OK
      : HttpStatus.UNAUTHORIZED;

    System.out.println(status);
    return new ResponseEntity<>("", status);
  }

  @GetMapping(value = "/logout")
  public RedirectView logout(HttpServletResponse response)
  {
    addTokenToHttpServletResponse(null, response);
    return new RedirectView(CONFIG.getHomeUrl());
  }  
  
  @RequestMapping(value = "/auth", method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public RedirectView auth(
    @CookieValue(value = AUTH_SERVICE_TOKEN,
                 defaultValue = UNAUTHORIZED) String tokenValue,
    @RequestParam(name = "username", defaultValue = "") String username,
    @RequestParam(name = "password", defaultValue = "") String password,
    HttpServletRequest request, HttpServletResponse response)
  {
    String homeUrl = CONFIG.getHomeUrl();

    RedirectView view = new RedirectView(homeUrl);

    System.out.println("- post auth : <" + request.getContextPath() + ">");

    AuthToken token = AuthToken.fromCookieValue(tokenValue);

    if (token != null)
    {
      token.setUid(username);

      if ( ldapAuth(username, password))
      {
        token.setAuthenticated(true);
        token.setLoginCounter(0);
      }
      else
      {
        token.incrementLoginCounter();
      }

      addTokenToHttpServletResponse(token, response);
      view = new RedirectView(homeUrl + token.getOriginalUri());
    }

    return view;
  }

  private boolean ldapAuth(String username, String password)
  {
    boolean authenticated = false;
    LDAPConnection connection = null;
    
    if ( ! Strings.isNullOrEmpty(username) 
      && ! Strings.isNullOrEmpty(password))
    {
    try
    {
      connection = LDAPConnectionFactory.getConnection();

      MessageFormat searchFormat = new MessageFormat(CONFIG.
        getLdapSearchFilter());
      
      String searchFilter = searchFormat.format(new Object[]
      {
        username
      });
      
      System.out.println(searchFilter);

      SearchScope scope;
      
      switch( CONFIG.getLdapSearchScope())
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
      
      SearchResult result = connection.search(CONFIG.getLdapBaseDn(),
        scope, searchFilter, CONFIG.getLdapSearchAttribute());
      List<SearchResultEntry> entries = result.getSearchEntries();
      if (entries.size() == 1)
      {
        SearchResultEntry entry = entries.get(0);
        System.out.println(entry.getDN());

        try
        {
          BindResult bindResult = connection.bind(entry.getDN(), password);
          if (bindResult.getResultCode().intValue() == 0)
          {
            authenticated = true;
            LOGGER.info("SUCCESS: bind dn={}", entry.getDN());
          }
        }
        catch (Exception e)
        {
          LOGGER.warn("WARNING: bind failed dn=" + entry.getDN());
        }
      }
      else
      {
        LOGGER.error("ERROR: wrong number of entries : " + entries.size());
      }
    }
    catch (LDAPException ex)
    {
      java.util.logging.Logger.getLogger(AuthController.class.getName()).
        log(Level.SEVERE, null, ex);
    }
    finally
    {
      if ( connection != null )
      {
        connection.close();
      }
    }
    }
    return authenticated;
  }
}
