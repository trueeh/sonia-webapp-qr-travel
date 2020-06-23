package sonia.webapp.qrtravel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.LoggerFactory;

/**
 *
 * @author th
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class QrTravelToken
{
  public final static String QR_TRAVEL_TOKEN = "QrTravelToken";

  public final static String UNKNOWN_TOKEN = "unknown";

  private final static Config CONFIG = Config.getInstance();

  private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    QrTravelToken.class.getName());

  private QrTravelToken()
  {
  }

  public void addToHttpServletResponse(HttpServletResponse response)
  {
    response.addCookie(toCookie());
    response.addHeader("Cache-Control",
      "no-cache, no-store, max-age=0, must-revalidate");
    response.addHeader("Pragma", "no-cache");
  }

  public static QrTravelToken fromCookieValue(String value)
  {
    QrTravelToken token = null;

    if (!UNKNOWN_TOKEN.equalsIgnoreCase(value))
    {
      ObjectMapper objectMapper = new ObjectMapper();
      try
      {
        value = Cipher.decrypt(value);
        token = objectMapper.readValue(value, QrTravelToken.class);
      }
      catch (Exception ex)
      {
        LOGGER.error("creating cookie ", ex);
      }
    }

    if (token == null)
    {
      token = new QrTravelToken();
    }

    return token;
  }

  public Cookie toCookie()
  {
    Cookie cookie = null;
    ObjectMapper objectMapper = new ObjectMapper();

    try
    {
      this.lastAccess = System.currentTimeMillis();
      String value = objectMapper.writeValueAsString(this);
      value = Cipher.encrypt(value);
      cookie = new Cookie(QR_TRAVEL_TOKEN, value);
      cookie.setPath("/");
      cookie.setMaxAge(CONFIG.getTokenTimeout());
      cookie.setHttpOnly(true);
    }
    catch (Exception ex)
    {
      LOGGER.error("creating cookie ", ex);
    }

    return cookie;
  }

  @Getter
  @Setter
  @JsonProperty("ml")
  private String mail;

  @Getter
  @Setter
  @JsonProperty("ph")
  private String phone;

  @Getter
  @Setter
  @JsonProperty("sn")
  private String surname;

  @Getter
  @Setter
  @JsonProperty("gn")
  private String givenName;

  @Getter
  @Setter
  @JsonProperty("lp")
  private String lastPin;
  
  @Getter
  @JsonProperty("ts")
  private long lastAccess;
  
  @Setter
  @Getter
  @JsonProperty("id")
  private String uid;
  
  @Setter
  @Getter
  @JsonProperty("pw")
  private String password;
  
  @Setter
  @Getter
  @JsonProperty("lc")
  private String location;
}
