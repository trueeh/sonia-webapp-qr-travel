package sonia.webapp.qrtravel.controller;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sonia.webapp.qrtravel.Config;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;
import sonia.webapp.qrtravel.ldap.LdapAccount;
import sonia.webapp.qrtravel.ldap.LdapUtil;
import sonia.webapp.qrtravel.ldap.LoginAttempt;

@RestController
public class ApiController
{
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

    if (Strings.isNullOrEmpty(request.getPhone()) || request.getPhone().length()
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
}
