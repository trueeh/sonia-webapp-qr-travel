/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import com.google.common.base.Strings;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
public class HomeController
{
  private final static SimpleDateFormat DATE_TIME = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ss");

  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(
    HomeController.class.getName());

  @GetMapping("/")
  public String home(
    @RequestParam(name = "p", required = false) String pin,
    @RequestParam(name = "l", required = false) String location,
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model)
  {
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);
    
    LOGGER.info("Home GET Request");
    LOGGER.info("pin = " + pin);

    if ( !Strings.isNullOrEmpty( token.getMail() ))
    {
      LOGGER.info("token = " + token.toString());
    }
    
    Attendee attendee = Database.lastAttendeeEntry(pin, token.getUuid());

    String submitButtonText = "Kommen";

    if (attendee != null && attendee.getDeparture() == null)
    {
      submitButtonText = "Gehen";
    }

    if (pin == null || !pin.equals(token.getLastPin()))
    {
      token.setLocation(null);
    }

    if ( attendee != null && token.getMail() != null && token.getMail().length() > 0)
    {
      LdapAccount account = LdapUtil.searchForMail(token.getMail());
      if (account != null)
      {
        if (account.getSoniaStudentNumber() != null)
        {
          attendee.setStudentnumber(account.getSoniaStudentNumber());
        }
        if (account.getMail() != null)
        {
          attendee.setEmail(account.getMail());
          token.setMail(account.getMail());
        }
        if (account.getUid() != null)
        {
          token.setUid(account.getUid());
        }
      }
      LOGGER.info(attendee.toString());
    }

    LOGGER.info(token.toString());

    if ( location != null )
    {
      token.setLocation(location);
    }
    
    model.addAttribute("attendeeInfo", new AttendeeInfo());
    model.addAttribute("room", Database.findRoom(pin));
    model.addAttribute("pin", pin);
    model.addAttribute("token", token);
    model.addAttribute("submitButtonText", submitButtonText);

    token.setLastPin(pin);
    token.addToHttpServletResponse(response);
    return "home";
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public String postHome(
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    @ModelAttribute AttendeeInfo attendeeInfo, HttpServletResponse response,
    HttpServletRequest request, Model model)
  {
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    LOGGER.info("Home POST Request");
    LOGGER.info("pin = " + attendeeInfo.getPin());
    LOGGER.info("token = " + token.toString());
    LOGGER.info("attendeeInfo = " + attendeeInfo.toString());

    Room room = Database.findRoom(attendeeInfo.getPin());
    Attendee attendee = Database.lastAttendeeEntry(attendeeInfo.getPin(), token.
      getUuid());

    if (attendee == null || attendee.getDeparture() != null)
    {
      attendee = new Attendee();
      attendee.setArrive(DATE_TIME.format(new Date()));
    }

    if (!Strings.isNullOrEmpty(attendeeInfo.getGivenName()))
    {
      token.setGivenName(attendeeInfo.getGivenName());
      attendee.setGivenname(attendeeInfo.getGivenName());
    }
    if (!Strings.isNullOrEmpty(attendeeInfo.getSurname()))
    {
      token.setSurname(attendeeInfo.getSurname());
      attendee.setSurname(attendeeInfo.getSurname());
    }
    if (!Strings.isNullOrEmpty(attendeeInfo.getMail()))
    {
      token.setMail(attendeeInfo.getMail().toLowerCase());
      attendee.setEmail(attendeeInfo.getMail().toLowerCase());
    }
    if (!Strings.isNullOrEmpty(attendeeInfo.getPhone()))
    {
      token.setPhone(attendeeInfo.getPhone());
      attendee.setPhonenumber(attendeeInfo.getPhone());
    }
    if (!Strings.isNullOrEmpty(attendeeInfo.getUid()))
    {
      token.setUid(attendeeInfo.getUid().toLowerCase());
    }
    if (!Strings.isNullOrEmpty(attendeeInfo.getPassword()))
    {
      token.setPassword(attendeeInfo.getPassword());
    }
    if (!Strings.isNullOrEmpty(attendeeInfo.getLocation()))
    {
      token.setLocation(attendeeInfo.getLocation());
      attendee.setLocation(attendeeInfo.getLocation());
    }
    if (!Strings.isNullOrEmpty(attendeeInfo.getPin()))
    {
      token.setLastPin(attendeeInfo.getPin());
      attendee.setPin(attendeeInfo.getPin());
    }

    if (attendeeInfo == null || attendeeInfo.getPin() == null
      || !attendeeInfo.getPin().equals(token.getLastPin()))
    {
      token.setLocation(null);
    }

    attendee.setCookieUUID(token.getUuid());

    if (attendee.getId() != 0 && attendee.getDeparture() == null)
    {
      attendee.setDeparture(DATE_TIME.format(new Date()));
    }

    String submitButtonText = "Kommen";

    if (attendee.getDeparture() == null)
    {
      submitButtonText = "Gehen";
    }

    if (room != null)
    {
      if (room.getRoomType().getRtype() != 1
        && token.getMail() != null && token.getMail().length() > 0)
      {
        LdapAccount account = LdapUtil.searchForMail(token.getMail());
        if (account != null)
        {
          if (account.getSoniaStudentNumber() != null)
          {
            attendee.setStudentnumber(account.getSoniaStudentNumber());
          }
          if (account.getMail() != null)
          {
            attendee.setEmail(account.getMail());
            token.setMail(account.getMail());
          }
          if (account.getUid() != null)
          {
            token.setUid(account.getUid());
          }
        }
      }
      else
      {
        if (attendeeInfo != null && attendeeInfo.getUid() != null
          && attendeeInfo.getPassword() != null && attendeeInfo.getPassword().
          length() > 0)
        {
          LdapAccount account = LdapUtil.bind(attendeeInfo.getUid(), 
             attendeeInfo.getPassword() );
          
          if ( account != null )
          {
            attendee.setEmail(account.getMail());
            token.setMail(account.getMail());
            attendee.setGivenname(account.getGivenName());
            token.setGivenName(account.getGivenName());
            attendee.setSurname(account.getSn());
            token.setSurname(account.getSn());
            attendee.setStudentnumber(account.getSoniaStudentNumber());
          }
          else
          {
            // error wrong credentials
          }
        }
        else
        {
          // error no credentials
        }
      }
    }
    
    
    LOGGER.info( attendee.toString() );
    
    if ( attendee.getEmail() != null )
    {
      Database.persist(attendee);
    }

    model.addAttribute("room", room );
    model.addAttribute("pin", attendeeInfo.getPin());
    model.addAttribute("token", token);
    model.addAttribute("submitButtonText", submitButtonText);
    model.addAttribute("dataCommitted", true); // TODO

    token.setLastPin(attendeeInfo.getPin());
    token.addToHttpServletResponse(response);
    return "home";
  }

}
