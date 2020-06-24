/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import static sonia.webapp.qrtravel.QrTravelToken.QR_TRAVEL_TOKEN;
import static sonia.webapp.qrtravel.QrTravelToken.UNKNOWN_TOKEN;
import sonia.webapp.qrtravel.db.Database;

@Controller
public class HomeController
{
  private final static Config CONFIG = Config.getInstance();
  private final static Logger LOGGER = LoggerFactory.getLogger(HomeController.class.getName());
  
  @GetMapping("/")
  public String home(
    @RequestParam( name="p", required = false ) String pin,
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model)
  {
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);
    
    LOGGER.info( "Home GET Request" );   
    LOGGER.info( "pin = " + pin );   
    LOGGER.info( "token = " + token.toString() );
 
/*
    token.setMail("t.ludewig@ostfalia.de");
    token.setPhone("+49 5331 939 19000");
    token.setGivenName("Thorsten");
    token.setSurname("Ludewig");
    token.setUid("th");
    token.setPassword("Hallo Welt!");
    token.setLocation("Irgendwo");
*/
    model.addAttribute("attendeeInfo", new AttendeeInfo());
    model.addAttribute("room", Database.findRoom(pin));
    model.addAttribute("pin", pin);
    model.addAttribute("token", token);
 
    token.setLastPin(pin);
    token.addToHttpServletResponse(response);
    return "home";
  }

  @RequestMapping( value = "/", method=RequestMethod.POST )
  public String postHome(
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    @ModelAttribute AttendeeInfo attendeeInfo, HttpServletResponse response, Model model)
  {
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);
    
    LOGGER.info( "Home POST Request" );   
    LOGGER.info( "pin = " + attendeeInfo.getPin() );   
    LOGGER.info( "token = " + token.toString() );
    LOGGER.info( "attendeeInfo = " + attendeeInfo.toString() );
 
    model.addAttribute("room", Database.findRoom(attendeeInfo.getPin()));
    model.addAttribute("pin", attendeeInfo.getPin());
    model.addAttribute("token", token);
 
    token.setLastPin(attendeeInfo.getPin());
    token.addToHttpServletResponse(response);
    return "home";
  }

}
