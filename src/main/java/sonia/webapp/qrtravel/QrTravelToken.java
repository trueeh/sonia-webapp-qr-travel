package sonia.webapp.qrtravel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.db.AttendeeData;
import sonia.webapp.qrtravel.form.AttendeeForm;
import sonia.webapp.qrtravel.form.ExamForm;
import sonia.webapp.qrtravel.form.RegistrationForm;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class QrTravelToken implements AttendeeData
{
  public final static String QR_TRAVEL_TOKEN = "QrTravelToken";

  public final static String UNKNOWN_TOKEN = "unknown";

  private final static Config CONFIG = Config.getInstance();

  private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    QrTravelToken.class.getName());

  private QrTravelToken()
  {
    uuid = UUID.randomUUID().toString();
  }

  public void setAttendeeData( AttendeeForm data )
  {
    this.phone = (Strings.isNullOrEmpty(data.getPhone())) ? phone : data.getPhone();
    this.city = (Strings.isNullOrEmpty(data.getCity())) ? city : data.getCity();
    this.street = (Strings.isNullOrEmpty(data.getStreet())) ? street : data.getStreet();
    this.location = (Strings.isNullOrEmpty(data.getLocation())) ? location : data.getLocation();

    if ( data instanceof RegistrationForm )
    {
      RegistrationForm form = (RegistrationForm)data;
      this.mail = (Strings.isNullOrEmpty(form.getMail())) ? mail : form.getMail();
      this.surname = (Strings.isNullOrEmpty(form.getSurname())) ? surname : form.getSurname();
      this.givenName = (Strings.isNullOrEmpty(form.getGivenName())) ? givenName : form.getGivenName();      
    } 
    else if ( data instanceof ExamForm )
    {
      ExamForm form = (ExamForm)data;
      this.uid = (Strings.isNullOrEmpty(form.getUserId())) ? uid : form.getUserId();
      this.password = (Strings.isNullOrEmpty(form.getPassword())) ? givenName : form.getPassword();      
    }
  }
  
  public void addToHttpServletResponse(HttpServletResponse response)
  {
    if (cookieAccepted)
    {
      response.addCookie(toCookie());
      response.addHeader("Cache-Control",
        "no-cache, no-store, max-age=0, must-revalidate");
      response.addHeader("Pragma", "no-cache");
    }
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
      if (!"".equals(CONFIG.getContextPath())) {
    	  cookie.setPath(CONFIG.getContextPath());
      } else {
    	  cookie.setPath("/");
      }
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
  @JsonProperty("ca")
  private boolean cookieAccepted;

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
  @ToString.Exclude
  private String uid;

  @Setter
  @Getter
  @JsonProperty("pw")
  @ToString.Exclude
  private String password;

  @Setter
  @Getter
  @JsonProperty("lc")
  private String location;
  
  @Setter
  @Getter
  @JsonProperty("st")
  private String street;
  
  @Setter
  @Getter
  @JsonProperty("ci")
  private String city;

  @Getter
  @JsonProperty("uu")
  private String uuid;
}
