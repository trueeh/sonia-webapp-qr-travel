package sonia.nginx.ldap.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author th
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AuthToken
{
  private AuthToken()
  {
  }
  
  private final static Config CONFIG = Config.getInstance();
  
  public AuthToken(String originalUri)
  {
    this.originalUri = originalUri;
  }
  
  public static AuthToken fromCookieValue(String value)
  {
    AuthToken session = null;
    
    if (!AuthController.UNAUTHORIZED.equalsIgnoreCase(value))
    {
      ObjectMapper objectMapper = new ObjectMapper();
      try
      {
        value = Cipher.decrypt(value);
        session = objectMapper.readValue(value, AuthToken.class);
      }
      catch (Exception ex)
      {
        Logger.getLogger(AuthToken.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    
    return session;
  }
  
  public static Cookie getLogoutCookie()
  {
    Cookie cookie = new AuthToken().toCookie();
    cookie.setMaxAge(1);
    return cookie;
  }
  
  public static AuthToken fromCookieValue(String value, String originalUri)
  {
    AuthToken token = fromCookieValue(value);
    
    if (token == null)
    {
      token = new AuthToken(originalUri);
    }
    
    return token;
  }
  
  public Cookie toCookie()
  {
    Cookie cookie = null;
    ObjectMapper objectMapper = new ObjectMapper();
    
    try
    {
      String value = objectMapper.writeValueAsString(this);
      value = Cipher.encrypt(value);      
      cookie = new Cookie(AuthController.AUTH_SERVICE_TOKEN, value);
      cookie.setPath("/");
      cookie.setMaxAge(CONFIG.getTokenTimeout());
      cookie.setHttpOnly(true);
    }
    catch (Exception ex)
    {
      Logger.getLogger(AuthToken.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return cookie;
  }
  
  public void incrementLoginCounter()
  {
    loginCounter++;
  }
  
  @Getter
  @Setter
  @JsonProperty("uri")
  private String originalUri;
  
  @Getter
  @Setter
  private String uid;
  
  @Getter
  @Setter
  @JsonProperty("auth")
  private boolean authenticated;
  
  @Getter
  @Setter
  @JsonProperty("cnt")
  private int loginCounter;
}
