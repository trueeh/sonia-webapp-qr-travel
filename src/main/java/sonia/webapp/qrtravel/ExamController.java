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
public class ExamController
{
  private final static String PW_IS_SET = "-Password Set-";

  private final static SimpleDateFormat DATE_TIME = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss");

  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(
    ExamController.class.getName());

  private static void exchangeData(ExamForm examForm,
    QrTravelToken token, Attendee attendee)
  {
    if (examForm != null && token != null)
    {
      // account id
      if (Strings.isNullOrEmpty(examForm.getUserId()))
      {
        examForm.setUserId(token.getUid());
      }
      else
      {
        token.setUid(examForm.getUserId());
      }

      // phone
      if (Strings.isNullOrEmpty(examForm.getPhone()))
      {
        examForm.setPhone(token.getPhone());
      }
      else
      {
        token.setPhone(examForm.getPhone());
      }

      // password
      if (Strings.isNullOrEmpty(examForm.getPassword()))
      {
        examForm.setPassword(token.getPassword());
      }
      else
      {
        token.setPassword(examForm.getPassword());
      }

      // location
      if (Strings.isNullOrEmpty(examForm.getLocation()))
      {
        examForm.setLocation(token.getLocation());
      }
      else
      {
        token.setLocation(examForm.getLocation());
      }

      if (attendee != null)
      {
        attendee.setCookieUUID(token.getUuid());
        attendee.setEmail(token.getMail());
        attendee.setSurname(token.getSurname());
        attendee.setGivenname(token.getGivenName());
        attendee.setLocation(token.getLocation());
        attendee.setPhonenumber(token.getPhone());
        attendee.setPin(examForm.getPin());
      }
    }
  }

  @GetMapping("/exam")
  public String exam(
    @RequestParam(name = "p", required = false) String pin,
    @RequestParam(name = "l", required = false) String location,
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, ExamForm examForm)
  {
    Room room = null;
    String submitButtonText = "Kommen";
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    LOGGER.debug("Home GET Request");
    LOGGER.debug("pin = " + pin);
    LOGGER.debug("location = " + location);

    if (!Strings.isNullOrEmpty(pin))
    {
      examForm.setPin(pin);

      if (!Strings.isNullOrEmpty(token.getMail()))
      {
        LOGGER.debug("Request token = " + token.toString());
      }

      if (!Strings.isNullOrEmpty(location))
      {
        token.setLocation(location);
      }

      exchangeData(examForm, token, null);

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
    return "exam";
  }

  @RequestMapping(value = "/exam", method = RequestMethod.POST)
  public String postExam(
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response,
    HttpServletRequest request, Model model,
    @Valid ExamForm examForm,
    BindingResult bindingResult)
  {
    String errorMessage = null;
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    LOGGER.debug("Home POST Request");
    LOGGER.debug("pin = " + examForm.getPin());
    LOGGER.debug("Request token = " + token.toString());
    LOGGER.debug("examForm = " + examForm.toString());

    if (PW_IS_SET.equalsIgnoreCase(examForm.getPassword()))
    {
      examForm.setPassword(token.getPassword());
    }

    Room room = Database.findRoom(examForm.getPin());
    Attendee attendee = null;

    token.setLocation(examForm.getLocation());

    boolean createEntry = false;

    if (!Strings.isNullOrEmpty(token.getUuid()))
    {
      attendee = Database.lastAttendeeEntry(examForm.getPin(),
        token.getUuid());

      if (attendee == null || attendee.getDeparture() != null)
      {
        createEntry = true;
        attendee = new Attendee();
        attendee.setArrive(DATE_TIME.format(new Date()));
      }

      LOGGER.debug("last db entry = " + attendee.toString());

      if (attendee.getId() != 0 && attendee.getDeparture() == null)
      {
        attendee.setDeparture(DATE_TIME.format(new Date()));
      }
    }

    exchangeData(examForm, token, attendee);

    LOGGER.trace("uid=" + examForm.getUserId() + " / " + token.getUid());
    LOGGER.trace("pwd=" + examForm.getPassword() + " / " + token.getPassword());

    boolean dataCommitted = false;

    if (bindingResult.hasErrors())
    {
      LOGGER.error("bind result has errors");
      List<FieldError> fel = bindingResult.getFieldErrors();
      for (FieldError fe : fel)
      {
        LOGGER.trace(fe.toString());
      }
    }
    else
    {
      LdapAccount account = LdapUtil.bind(examForm.getUserId(),
        examForm.getPassword());

      if (account != null)
      {
        attendee.setEmail(account.getMail());
        token.setMail(account.getMail());
        attendee.setGivenname(account.getGivenName());
        token.setGivenName(account.getGivenName());
        attendee.setSurname(account.getSn());
        token.setSurname(account.getSn());
        attendee.setStudentnumber(account.getSoniaStudentNumber());

        //
        Database.persist(attendee);
        examForm.setPassword(PW_IS_SET);
        dataCommitted = true;
      }
      else
      {
        errorMessage = "Zugangsdaten falsch!";
      }
    }

    model.addAttribute("room", room);
    model.addAttribute("pin", examForm.getPin());
    model.addAttribute("submitButtonText",
      (createEntry ^ dataCommitted) ? "Kommen" : "Gehen");
    model.addAttribute("dataCommitted", dataCommitted);
    model.addAttribute("errorMessage", errorMessage);

    token.setLastPin(examForm.getPin());
    token.addToHttpServletResponse(response);

    LOGGER.debug("Response token = " + token.toString());
    return "exam";
  }

}
