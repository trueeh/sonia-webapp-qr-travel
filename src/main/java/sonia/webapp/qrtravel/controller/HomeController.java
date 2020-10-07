package sonia.webapp.qrtravel.controller;

import com.google.common.base.Strings;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sonia.webapp.qrtravel.Config;
import sonia.webapp.qrtravel.QrTravelToken;
import static sonia.webapp.qrtravel.QrTravelToken.QR_TRAVEL_TOKEN;
import static sonia.webapp.qrtravel.QrTravelToken.UNKNOWN_TOKEN;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;

@Controller
public class HomeController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    HomeController.class.getName());

  private final static Config CONFIG = Config.getInstance();

  @GetMapping("/kaputt")
  public void kaputt()
  {
    throw new RuntimeException("kaputt");
  }
  
  @GetMapping("/")
  public String home(
    @RequestParam(name = "p", required = false) String pin,
    @RequestParam(name = "l", required = false) String location,
    @CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model)
  {
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);

    LOGGER.debug("Home GET Request");
    LOGGER.debug("pin = " + pin);

    if (!Strings.isNullOrEmpty(token.getMail()))
    {
      LOGGER.debug("token = " + token.toString());
    }

    Room room = null;

    if (!Strings.isNullOrEmpty(pin))
    {
      room = Database.findRoom(pin);
    }

    if (location != null)
    {
      token.setLocation(location);
    }

    model.addAttribute("room", room);
    model.addAttribute("pin", pin);
    model.addAttribute("token", token);
	model.addAttribute("config", CONFIG);

    LOGGER.debug("Response token = " + token.toString());

    token.addToHttpServletResponse(response);

    if (room != null)
    {
      if (room.getRoomType().getRtype() != 1)
      {
        return "redirect:/registration?p=" + pin + ((location != null) ? ("&l="
          + location) : "");
      }
      else
      {
        return "redirect:/exam?p=" + pin + ((location != null) ? ("&l="
          + location) : "");
      }
    }

    return "home";
  }
}
