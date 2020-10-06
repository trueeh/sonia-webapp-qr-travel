package sonia.webapp.qrtravel.controller.admin;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
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
import sonia.webapp.qrtravel.form.RoomPinForm;
import sonia.webapp.qrtravel.util.Counter;
import sonia.webapp.qrtravel.util.ErrorMessage;

/**
 *
 * @author Dr.-Ing. Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@Controller
@Scope("session")
public class AdminController
{
  private final static String QR_TRAVEL_ERROR_MESSAGE = "QR_TRAVEL_ERROR_MESSAGE";

  private final static Logger LOGGER = LoggerFactory.getLogger(
    AdminController.class.getName());

  private final static Config CONFIG = Config.getInstance();

  private final Random random = new Random(System.currentTimeMillis());

  private String uuid = UUID.randomUUID().toString();

  @GetMapping("/admin")
  public String httpGetAdminPage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, HttpServletRequest request, Model model,
    AdminRoomForm adminRoomForm,
    RoomPinForm roomPinForm)
  {
    LOGGER.debug("Admin home GET request ({})", uuid);
    QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);

    ErrorMessage errorMessage = (ErrorMessage) request.getSession(true).
      getAttribute(QR_TRAVEL_ERROR_MESSAGE);
    LOGGER.debug("Error message ({})", errorMessage);

    model.addAttribute("roomTypes", Database.listRoomTypes());
    model.addAttribute("rooms", Database.listRooms());
    model.addAttribute("token", token);
    model.addAttribute("config", CONFIG);
    model.addAttribute("counter", new Counter());
    model.addAttribute("errorMessage", errorMessage);
    token.addToHttpServletResponse(response);
    return "adminHome";
  }

  @PostMapping("/admin")
  public String httpPostAdminPage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model,
    @Valid AdminRoomForm adminRoomForm, RoomPinForm roomPinForm,
    BindingResult bindingResult)
  {
    LOGGER.debug("Admin home POST request ({})", uuid);
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
    model.addAttribute("counter", new Counter());
    token.addToHttpServletResponse(response);
    return "adminHome";
  }

  @PostMapping("/admin/deleteRoom")
  public String httpPostAdminDeleteRoom(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, HttpServletRequest request, Model model,
    RoomPinForm roomPinForm)
  {
    LOGGER.debug("Admin httpPostAdminDeleteRoom POST request  ({})", uuid);
    QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);

    LOGGER.debug("deleting room pin={}", roomPinForm.getPin());

    ErrorMessage errorMessage = null;

    String pin = roomPinForm.
      getPin();

    Room room = Database.findRoom(pin);

    if (room == null)
    {
      errorMessage = new ErrorMessage("Fehler!", "Raum #" + pin
        + " nicht gefunden");
    }
    else
    {

      if (room.getAttendees().size() > 0)
      {
        errorMessage = new ErrorMessage("Hinweis:", "Raum #" + pin
          + " enthält noch " + room.getAttendees().size()
          + " Einträge und kann deshalb nicht gelöscht werden."
          + " Sollten keine neuen Einträge hinzu kommen, kann dieser Raum in spätestens "
          + CONFIG.getExpirationTimeInDays() + " Tagen gelöscht werden."
        );
      }
      else
      {
        Database.deleteRoom(pin);
        errorMessage = new ErrorMessage("Hinweis:", "Raum #" + pin
          + ", '" + room.getDescription()
          + "' (" + room.getRoomType().getDescription() + ") wurde gelöscht."
        );
      }
    }

    request.getSession(true).setAttribute(QR_TRAVEL_ERROR_MESSAGE, errorMessage);
    token.addToHttpServletResponse(response);
    return "redirect:/admin";
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

  // private ErrorMessage errorMessage = new ErrorMessage();
}
