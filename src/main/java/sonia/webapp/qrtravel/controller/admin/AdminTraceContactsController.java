package sonia.webapp.qrtravel.controller.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import sonia.webapp.qrtravel.Config;
import sonia.webapp.qrtravel.QrTravelAdminToken;
import static sonia.webapp.qrtravel.QrTravelAdminToken.QR_TRAVEL_ADMIN_TOKEN;
import static sonia.webapp.qrtravel.QrTravelAdminToken.UNKNOWN_ADMIN_TOKEN;
import sonia.webapp.qrtravel.form.AdminRoomForm;
import sonia.webapp.qrtravel.form.RoomPinForm;
import sonia.webapp.qrtravel.util.ErrorMessage;

/**
 *
 * @author Dr.-Ing. Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@Controller
@Scope("session")
public class AdminTraceContactsController
{
  private final static String QR_TRAVEL_ERROR_MESSAGE = "QR_TRAVEL_ERROR_MESSAGE";

  private final static Logger LOGGER = LoggerFactory.getLogger(AdminTraceContactsController.class.getName());

  private final static Config CONFIG = Config.getInstance();

  @GetMapping("/admin/trace-contacts")
  public String httpGetAdminPage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, HttpServletRequest request, Model model,
    AdminRoomForm adminRoomForm,
    RoomPinForm roomPinForm)
  {
    LOGGER.debug("Admin trace contacts GET request");
    QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);

    ErrorMessage errorMessage = (ErrorMessage) request.getSession(true).
      getAttribute(QR_TRAVEL_ERROR_MESSAGE);
    LOGGER.debug("Error message ({})", errorMessage);

    model.addAttribute("config", CONFIG);
    model.addAttribute("token", token);
    model.addAttribute("errorMessage", errorMessage);
    token.addToHttpServletResponse(response);
    return "adminTrace";
  }
}
