package sonia.webapp.qrtravel.controller.admin;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import sonia.webapp.qrtravel.QrTravelAdminToken;
import static sonia.webapp.qrtravel.QrTravelAdminToken.QR_TRAVEL_ADMIN_TOKEN;
import static sonia.webapp.qrtravel.QrTravelAdminToken.UNKNOWN_ADMIN_TOKEN;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.form.LoginForm;

/**
 *
 * @author Dr. Thorsten Ludewig <t.ludewig@gmail.com>
 */
@Controller
public class AdminController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(AdminController.class.getName());

  @GetMapping("/admin")
  public String httpGetHomePage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, LoginForm loginForm)
  {
    LOGGER.debug("Admin home GET request");
    QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);
    
    model.addAttribute("roomTypes", Database.listRoomTypes());
    model.addAttribute("rooms", Database.listRooms());
    model.addAttribute("token", token);
    token.addToHttpServletResponse(response);
    return "adminHome";
  }

}
