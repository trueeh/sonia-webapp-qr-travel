/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

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

      if (attendee != null)
      {
        attendee.setCookieUUID(token.getUuid());
        attendee.setEmail(token.getMail());
        attendee.setSurname(token.getSurname());
        attendee.setGivenname(token.getGivenName());
        attendee.setLocation(token.getLocation());
        attendee.setPhonenumber(token.getPhone());
        attendee.setPin(registrationForm.getPin());
      }
    }
  }

  @GetMapping("/registration")
  public String registration(
    @RequestParam(name = "p", required = false) String pin,
    @RequestParam(name = "l", required = false) String location,
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, RegistrationForm registrationForm)
  {
    Room room = null;
    String submitButtonText = "Kommen";
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    LOGGER.debug("Home GET Request");
    LOGGER.debug("pin = " + pin);
    LOGGER.debug("location = " + location);

    if (!Strings.isNullOrEmpty(pin))
    {
      registrationForm.setPin(pin);

      if (!Strings.isNullOrEmpty(token.getMail()))
      {
        LOGGER.debug("Request token = " + token.toString());
      }

      if (!Strings.isNullOrEmpty(location))
      {
        token.setLocation(location);
      }

      exchangeData(registrationForm, token, null);

      if (!Strings.isNullOrEmpty(pin))
      {
        room = Database.findRoom(pin);
      }

      if (!Strings.isNullOrEmpty(token.getUuid()))
      {
        Attendee attendee = Database.lastAttendeeEntry(pin, token.getUuid());

        if (attendee != null)
        {
          LOGGER.debug("last db entry = " + attendee.toString());

          if (attendee.getDeparture() == null)
          {
            submitButtonText = "Gehen";
          }
        }
      }
    }

    model.addAttribute("room", room);
    model.addAttribute("pin", pin);
    model.addAttribute("submitButtonText", submitButtonText);

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

    LOGGER.debug("Home POST Request");
    LOGGER.debug("pin = " + registrationForm.getPin());
    LOGGER.debug("Request token = " + token.toString());
    LOGGER.debug("registrationForm = " + registrationForm.toString());

    Room room = Database.findRoom(registrationForm.getPin());

    String submitButtonText = "Kommen";
    Attendee attendee = null;

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
          attendee = new Attendee();
          attendee.setArrive(DATE_TIME.format(new Date()));
        }
        if (attendee.getId() != 0 && attendee.getDeparture() == null)
        {
          attendee.setDeparture(DATE_TIME.format(new Date()));
        }
      }
    }

    exchangeData(registrationForm, token, attendee);

    if (attendee != null && attendee.getDeparture() == null)
    {
      submitButtonText = "Gehen";
    }

    boolean dataCommitted = false;

    if (bindingResult.hasErrors())
    {
      LOGGER.error("bind result has errors");
      List<FieldError> fel = bindingResult.getFieldErrors();
      for (FieldError fe : fel)
      {
        LOGGER.error(fe.toString());
      }
    }
    else
    {
      if (attendee != null && attendee.getEmail() != null)
      {
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

    model.addAttribute("room", room);
    model.addAttribute("pin", registrationForm.getPin());
    model.addAttribute("submitButtonText", submitButtonText);
    model.addAttribute("dataCommitted", dataCommitted);

    token.setLastPin(registrationForm.getPin());
    token.addToHttpServletResponse(response);

    LOGGER.debug("Response token = " + token.toString());
    return "registration";
  }

}
