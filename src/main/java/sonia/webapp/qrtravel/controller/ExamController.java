/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import sonia.webapp.qrtravel.form.ExamForm;
import sonia.webapp.qrtravel.QrTravelToken;
import static sonia.webapp.qrtravel.QrTravelToken.QR_TRAVEL_TOKEN;
import static sonia.webapp.qrtravel.QrTravelToken.UNKNOWN_TOKEN;
import sonia.webapp.qrtravel.db.Attendee;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;
import sonia.webapp.qrtravel.ldap.LdapAccount;
import sonia.webapp.qrtravel.ldap.LdapUtil;
import sonia.webapp.qrtravel.ldap.LoginAttempt;

@Controller
public class ExamController
{
  private final static String PW_IS_SET = "-Password Set-";

  private final static Config CONFIG = Config.getInstance();

  private final static SimpleDateFormat DATE_TIME = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss");

  private final static Logger LOGGER = LoggerFactory.getLogger(
    ExamController.class.getName());

 
  @GetMapping("/exam")
  public String exam(
    @RequestParam(name = "p", required = false) String pin,
    @RequestParam(name = "l", required = false) String location,
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, ExamForm examForm)
  {
    Room room = null;
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);
    boolean createEntry = false;

    LOGGER.debug("Exam GET Request");
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
      
      examForm.setAttendeeData(pin, token);
      room = Database.findRoom(pin);
        
      if (!Strings.isNullOrEmpty(token.getUuid()))
      {
        Attendee attendee = Database.lastAttendeeEntry(pin, token.getUuid());

        if (attendee != null)
        {
          LOGGER.debug("last db entry = " + attendee.toString());

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

    model.addAttribute("room", room);
    model.addAttribute("pin", pin);
    model.addAttribute("token", token);
    model.addAttribute("submitButtonText", createEntry ? "Kommen" : "Gehen");

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

    LOGGER.debug("Exam POST Request");
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

    token.setAttendeeData(examForm);
    attendee.setAttendeeData(examForm.getPin(), token);

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
      LoginAttempt loginAttempt = LdapUtil.getLoginAttempt(examForm.getUserId());
      LOGGER.debug("loginAttempt=" + loginAttempt);
      if (loginAttempt != null && loginAttempt.getCounter() < CONFIG.
        getMaxLoginAttempts())
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

          Database.persist(attendee);
          examForm.setPassword(PW_IS_SET);
          dataCommitted = true;
        }
        else
        {
          loginAttempt.incrementCounter();
          errorMessage = "Zugangsdaten falsch!";
        }
      }
      else
      {
        errorMessage = "Dieser Account ist für " + CONFIG.
          getLoginFailedBlockingDuration() + "s gesperrt!";
      }
    }

    model.addAttribute("room", room);
    model.addAttribute("token", token);
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
