package sonia.webapp.qrtravel.controller;

import com.google.common.base.Strings;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sonia.webapp.qrtravel.Config;
import sonia.webapp.qrtravel.QrTravelToken;
import sonia.webapp.qrtravel.form.RegistrationForm;
import static sonia.webapp.qrtravel.QrTravelToken.QR_TRAVEL_TOKEN;
import static sonia.webapp.qrtravel.QrTravelToken.UNKNOWN_TOKEN;
import sonia.webapp.qrtravel.db.Attendee;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;
import sonia.webapp.qrtravel.ldap.LdapAccount;
import sonia.webapp.qrtravel.ldap.LdapUtil;

@Controller
public class RegistrationController
{
  private final static SimpleDateFormat DATE_TIME = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss");

  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(
    RegistrationController.class.getName());

  /*
  private static void exchangeData(RegistrationForm registrationForm,
    QrTravelToken token, Attendee attendee)
  {
    if (registrationForm != null && token != null)
    {
      // mail
      if (Strings.isNullOrEmpty(registrationForm.getMail()))
      {
        registrationForm.setMail(token.getMail());
      }
      else
      {
        token.setMail(registrationForm.getMail());
      }

      // phone
      if (Strings.isNullOrEmpty(registrationForm.getPhone()))
      {
        registrationForm.setPhone(token.getPhone());
      }
      else
      {
        token.setPhone(registrationForm.getPhone());
      }

      // surname
      if (Strings.isNullOrEmpty(registrationForm.getSurname()))
      {
        registrationForm.setSurname(token.getSurname());
      }
      else
      {
        token.setSurname(registrationForm.getSurname());
      }

      // given name
      if (Strings.isNullOrEmpty(registrationForm.getGivenName()))
      {
        registrationForm.setGivenName(token.getGivenName());
      }
      else
      {
        token.setGivenName(registrationForm.getGivenName());
      }

      // location
      if (Strings.isNullOrEmpty(registrationForm.getLocation()))
      {
        registrationForm.setLocation(token.getLocation());
      }
      else
      {
        token.setLocation(registrationForm.getLocation());
      }

            // street
      if (Strings.isNullOrEmpty(registrationForm.getStreet()))
      {
        registrationForm.setStreet(token.getStreet());
      }
      else
      {
        token.setStreet(registrationForm.getStreet());
      }

      // city
      if (Strings.isNullOrEmpty(registrationForm.getCity()))
      {
        registrationForm.setCity(token.getCity());
      }
      else
      {
        token.setCity(registrationForm.getCity());
      }

      if (attendee != null)
      {
        attendee.setAttendeeData(registrationForm.getPin(), token);
      }
    }
  }
*/
  
  
  @GetMapping("/registration")
  public String registration(
    @RequestParam(name = "p", required = false) String pin,
    @RequestParam(name = "l", required = false) String location,
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, RegistrationForm registrationForm)
  {
    Room room = null;
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    LOGGER.debug("Registration GET Request");
    LOGGER.debug("pin = " + pin);
    LOGGER.debug("location = " + location);

    boolean createEntry = false;

    if (!Strings.isNullOrEmpty(pin))
    {
      if (!Strings.isNullOrEmpty(token.getMail()))
      {
        LOGGER.debug("Request token = " + token.toString());
      }

      if (!Strings.isNullOrEmpty(location))
      {
        token.setLocation(location);
      }
      else
      {
        if ( token.getLastPin() == null || !token.getLastPin().equalsIgnoreCase(pin) )
        {
          token.setLocation(null);
        }
      }

      registrationForm.setAttendeeData(pin, token);
      room = Database.findRoom(pin);
      
      if (!Strings.isNullOrEmpty(token.getUuid()))
      {
        Attendee attendee = Database.lastAttendeeEntry(pin, token.getUuid());

        if (attendee != null)
        {
          LOGGER.debug("last db entry = " + attendee );

          if (attendee.getDeparture() != null)
          {
            createEntry = true;
          }
        }
        else
        {
          createEntry = true;
        }
      }
    }
    
    LOGGER.debug( "create entry = " + createEntry );

    model.addAttribute("room", room);
    model.addAttribute("pin", pin);
    model.addAttribute("token", token);
    model.addAttribute("submitButtonText", ( createEntry ? "Kommen" : "Gehen"));

    token.setLastPin(pin);
    LOGGER.debug("Response token = " + token.toString());

    token.addToHttpServletResponse(response);
    return "registration";
  }

  @RequestMapping(value = "/registration", method = RequestMethod.POST)
  public String postRegistration(
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response,
    HttpServletRequest request, Model model,
    @Valid RegistrationForm registrationForm,
    BindingResult bindingResult)
  {
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    LOGGER.debug("Registration POST Request");
    LOGGER.debug("pin = " + registrationForm.getPin());
    LOGGER.debug("Request token = " + token.toString());
    LOGGER.debug("registrationForm = " + registrationForm.toString());

    Room room = Database.findRoom(registrationForm.getPin());

    Attendee attendee = null;
    boolean createEntry = false;

    token.setLocation(registrationForm.getLocation());

    if (!Strings.isNullOrEmpty(token.getUuid()))
    {
      attendee = Database.lastAttendeeEntry(registrationForm.getPin(),
        token.getUuid());

      LOGGER.debug("last db entry = " + attendee );

      if (!bindingResult.hasErrors())
      {
        if (attendee == null || attendee.getDeparture() != null)
        {
          createEntry = true;
          attendee = new Attendee();
          attendee.setArrive(DATE_TIME.format(new Date()));
        }
        if (attendee.getId() != 0 && attendee.getDeparture() == null)
        {
          attendee.setDeparture(DATE_TIME.format(new Date()));
        }
      }
      else
      {
        createEntry = true;
      }
    }

    LOGGER.debug( "rf=" + registrationForm );
    LOGGER.debug( "tok=" + token );
    LOGGER.debug( "attendee=" + attendee );
    
    token.setAttendeeData(registrationForm);
  
    if ( attendee != null )
    {
      attendee.setAttendeeData(registrationForm.getPin(), token);
    }
    
    boolean dataCommitted = false;

    if (bindingResult.hasErrors())
    {
      LOGGER.trace("bind result has errors");
      List<FieldError> fel = bindingResult.getFieldErrors();
      for (FieldError fe : fel)
      {
        LOGGER.trace(fe.toString());
      }
    }
    else
    {
      LOGGER.debug("1");
      if (attendee != null && attendee.getEmail() != null)
      {
        LOGGER.debug("2");
        LdapAccount account = LdapUtil.searchForMail(attendee.getEmail());
        if (account != null)
        {
          if (account.getSoniaStudentNumber() != null)
          {
            attendee.setStudentnumber(account.getSoniaStudentNumber());
          }
          if (account.getMail() != null)
          {
            LOGGER.debug("Setting eMail to: " + account.getMail());
            attendee.setEmail(account.getMail());
            token.setMail(account.getMail());
            registrationForm.setMail(account.getMail());
          }
          if (account.getUid() != null)
          {
            token.setUid(account.getUid());
          }
        }
        
        Database.persist(attendee);
      }
      dataCommitted = true;
    }
    
    LOGGER.debug( "create entry = " + createEntry + ", data commited = " + dataCommitted );
    
    model.addAttribute("room", room);
    model.addAttribute("token", token);
    model.addAttribute("pin", registrationForm.getPin());
    model.addAttribute("submitButtonText",
      (createEntry ^ dataCommitted) ? "Kommen" : "Gehen");
    model.addAttribute("dataCommitted", dataCommitted);

    token.setLastPin(registrationForm.getPin());
    token.addToHttpServletResponse(response);

    LOGGER.debug("Response token = " + token.toString());
    return "registration";
  }

}
