package sonia.webapp.qrtravel.controller.admin;

import java.util.List;
import java.util.Random;
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
import org.springframework.web.bind.annotation.PostMapping;
import sonia.webapp.qrtravel.Config;
import sonia.webapp.qrtravel.QrTravelAdminToken;
import static sonia.webapp.qrtravel.QrTravelAdminToken.QR_TRAVEL_ADMIN_TOKEN;
import static sonia.webapp.qrtravel.QrTravelAdminToken.UNKNOWN_ADMIN_TOKEN;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;
import sonia.webapp.qrtravel.form.AdminRoomForm;

/**
 *
 * @author Dr. Thorsten Ludewig <t.ludewig@gmail.com>
 */
@Controller
public class AdminController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    AdminController.class.getName());

  private final static Config CONFIG = Config.getInstance();

  private final Random random = new Random(System.currentTimeMillis());

  @GetMapping("/admin")
  public String httpGetAdminPage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, AdminRoomForm adminRoomForm)
  {
    LOGGER.debug("Admin home GET request");
    QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);

    model.addAttribute("roomTypes", Database.listRoomTypes());
    model.addAttribute("rooms", Database.listRooms());
    model.addAttribute("token", token);
    model.addAttribute("config", CONFIG);
    token.addToHttpServletResponse(response);
    return "adminHome";
  }

  @PostMapping("/admin")
  public String httpPostAdminPage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model,
    @Valid AdminRoomForm adminRoomForm,
    BindingResult bindingResult)
  {
    LOGGER.debug("Admin home POST request");
    QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);

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
      LOGGER.debug(adminRoomForm.toString());

      String newPin = null;
      boolean valid = true;

      do
      {
        newPin = randomPin();
        for (Room r : Database.listRooms())
        {
          if (r.getPin().equals(newPin))
          {
            valid = false;
            break;
          }
        }
      }
      while (!valid);

      LOGGER.debug("newPin=" + newPin);

      Room room = new Room(newPin, adminRoomForm.getRoomType(), adminRoomForm.
        getDescription(), token.getUid(), CONFIG.getDomain());
      
      Database.persist(room);
    }

    adminRoomForm.setRoomType(0);
    adminRoomForm.setDescription(null);
    model.addAttribute("roomTypes", Database.listRoomTypes());
    model.addAttribute("rooms", Database.listRooms());
    model.addAttribute("token", token);
    model.addAttribute("config", CONFIG);
    token.addToHttpServletResponse(response);
    return "adminHome";
  }

  private String randomPin()
  {
    String pin = "";
    for (int i = 0; i < 6; i++)
    {
      pin += random.nextInt(10);
    }
    LOGGER.debug("random pin=" + pin);
    return pin;
  }
}
