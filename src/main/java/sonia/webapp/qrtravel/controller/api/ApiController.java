package sonia.webapp.qrtravel.controller.api;

import sonia.webapp.qrtravel.api.ApiResponse;
import sonia.webapp.qrtravel.api.ApiRequest;
import com.google.common.base.Strings;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sonia.webapp.qrtravel.Config;
import sonia.webapp.qrtravel.api.ApiCardRequest;
import sonia.webapp.qrtravel.api.ApiCardResponse;
import sonia.webapp.qrtravel.db.Attendee;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;
import sonia.webapp.qrtravel.ldap.LdapAccount;
import sonia.webapp.qrtravel.ldap.LdapUtil;
import sonia.webapp.qrtravel.ldap.LoginAttempt;

@RestController
public class ApiController
{
  private final static SimpleDateFormat DATE_TIME = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss");
  
  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(
    ApiController.class.getName());

  @PostMapping(path = "/api/checkin",
               consumes = MediaType.APPLICATION_JSON_VALUE,
               produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse apiCheckiIn(@RequestBody ApiRequest request)
  {
    LOGGER.debug("/api/checkin");
    LOGGER.trace("api request = " + request.toString());

    ApiResponse response = new ApiResponse(ApiResponse.ERROR,
      "Unbekannter Fehler");

    if (Strings.isNullOrEmpty(request.getAuthToken())
      || !request.getAuthToken().equalsIgnoreCase(CONFIG.getApiAuthToken()))
    {
      response = new ApiResponse(ApiResponse.ERROR,
        "Missing or wrong auth token");
    }
    else
    {
      if (Strings.isNullOrEmpty(request.getPhone()) || request.getPhone().
        length()
        < 4)
      {
        response = new ApiResponse(ApiResponse.PHONENUMBER_IS_MISSING,
          "Telefonnummer (minimum 4 Zahlen)");
      }
      else
      {
        if (!Strings.isNullOrEmpty(request.getPin()))
        {
          Room room = Database.findRoom(request.getPin());

          if (room != null)
          {
            if (!Strings.isNullOrEmpty(request.getUsername())
              && !Strings.isNullOrEmpty(request.getPassword()))
            {
              LoginAttempt loginAttempt = LdapUtil.
                getLoginAttempt(request.getUsername());
              LOGGER.debug("loginAttempt=" + loginAttempt);
              if (loginAttempt != null && loginAttempt.getCounter() < CONFIG.
                getMaxLoginAttempts())
              {
                LdapAccount account = LdapUtil.bind(request.getUsername(),
                  request.getPassword());

                if (account != null)
                {
                  response = new ApiResponse(ApiResponse.OK, "OK");
                }
                else
                {
                  loginAttempt.incrementCounter();
                  response = new ApiResponse(ApiResponse.INVALID_CREDENTIALS,
                    "Zugangsdaten falsch!");
                }
              }
              else
              {
                response = new ApiResponse(ApiResponse.ACCOUNT_BLOCKED,
                  "Dieser Account ist für " + CONFIG.
                    getLoginFailedBlockingDuration() + "s gesperrt!");
              }
            }
          }
          else
          {
            response = new ApiResponse(ApiResponse.UNKNOWN_ROOM,
              "Unbekannter Raum!");
          }
        }
      }
    }

    return response;
  }

  @PostMapping(path = "/api/checkout",
               consumes = MediaType.APPLICATION_JSON_VALUE,
               produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse apiCheckOut(@RequestBody ApiRequest request)
  {
    LOGGER.debug("/api/checkout");
    LOGGER.trace("api request = " + request.toString());
    return new ApiResponse(0, "OK");
  }

  @PostMapping(path = "/api/card",
               consumes = MediaType.APPLICATION_JSON_VALUE,
               produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiCardResponse apiCard(@RequestBody ApiCardRequest request)
  {
    LOGGER.debug("/api/card");
    LOGGER.trace("api request = " + request.toString());

    ApiCardResponse response = new ApiCardResponse(ApiResponse.ERROR,
      "Unbekannter Fehler");

    if (Strings.isNullOrEmpty(request.getAuthToken())
      || !request.getAuthToken().equalsIgnoreCase(CONFIG.getApiAuthToken()))
    {
      response = new ApiCardResponse(ApiResponse.ERROR,
        "Missing or wrong auth token");
    }
    else
    {

      if (!Strings.isNullOrEmpty(request.getPin()))
      {
        Room room = Database.findRoom(request.getPin());

        if (room != null && request.getCardSerialNumber() > 0)
        {
          LdapAccount ldapAccount = LdapUtil.searchForCard(request.getCardSerialNumber());
          
          if ( ldapAccount != null )
          {
            LOGGER.debug(ldapAccount.toString());
            response = new ApiCardResponse(ApiResponse.OK,
            "OK - " + (request.isPresent() ? "kommen" : "gehen" ));
            response.setSn( ldapAccount.getSn() );
            response.setGivenName(ldapAccount.getGivenName() );
            response.setEmployeeType(ldapAccount.getEmployeeType() );
            response.setJpegPhoto(ldapAccount.getJpegPhoto() );
            response.setMail(ldapAccount.getMail() );
            response.setOu(ldapAccount.getOu() );
            response.setSoniaStudentNumber(ldapAccount.getSoniaStudentNumber() );
            response.setSoniaChipcardBarcode(ldapAccount.getSoniaChipcardBarcode() );
            response.setUid(ldapAccount.getUid() );
            
            // 
            
            Attendee attendee = null;
            
            if ( request.isPresent() )
            {
              attendee = new Attendee();
              attendee.setPin(request.getPin());
              attendee.setArrive(DATE_TIME.format(new Date()));
              attendee.setCookieUUID(Long.toString(request.getCardSerialNumber()));
              attendee.setEmail(ldapAccount.getMail());
              attendee.setSurname(ldapAccount.getSn());
              attendee.setGivenname(ldapAccount.getGivenName());
              attendee.setStudentnumber(ldapAccount.getSoniaStudentNumber());
              attendee.setPhonenumber("smartcard");
            }
            else
            {
              attendee = Database.lastAttendeeEntry(request.getPin(), Long.toString(request.getCardSerialNumber()));
              if ( attendee != null )
              {
                if ( Strings.isNullOrEmpty(attendee.getDeparture()))
                {
                  attendee.setDeparture(DATE_TIME.format(new Date()));
                }
              }
              else
              {
                LOGGER.debug( "Attendee not found" );
              }
            }
            
            if ( attendee != null )
            {
              Database.persist(attendee);
            }
          }
          else
          {
            LOGGER.info( "Kartennummer: " + request.getCardSerialNumber()+ " nicht gefunden." );
            response = new ApiCardResponse(ApiResponse.ERROR,
            "Kartennummer nicht gefunden.");
          }
        }
        else
        {
          LOGGER.info( "Unbekannter Raum: " + request.getPin() );
          response = new ApiCardResponse(ApiResponse.UNKNOWN_ROOM,
            "Unbekannter Raum!");
        }
      }
    }
    return response;
  }

}
