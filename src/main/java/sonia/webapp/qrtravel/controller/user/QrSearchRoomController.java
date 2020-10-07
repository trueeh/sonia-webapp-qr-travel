package sonia.webapp.qrtravel.controller.user;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import sonia.webapp.qrtravel.Config;
import sonia.webapp.qrtravel.QrTravelAdminToken;
import static sonia.webapp.qrtravel.QrTravelAdminToken.QR_TRAVEL_ADMIN_TOKEN;
import static sonia.webapp.qrtravel.QrTravelAdminToken.UNKNOWN_ADMIN_TOKEN;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;
import sonia.webapp.qrtravel.db.RoomType;
import sonia.webapp.qrtravel.form.AdminRoomForm;
import sonia.webapp.qrtravel.form.SearchRoomForm;
import sonia.webapp.qrtravel.util.Counter;

/**
 * search functions for room list
 * @author trueeh
 * 
 */
@Controller
public class QrSearchRoomController {
	private final static Config CONFIG = Config.getInstance();

	private final static Logger LOGGER = LoggerFactory.getLogger(QrSearchRoomController.class.getName());

	@GetMapping("/search")
	public String httpGetSearchPage(
			@CookieValue(value = QR_TRAVEL_ADMIN_TOKEN, defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
			HttpServletResponse response, Model model, SearchRoomForm searchRoomForm) {
		LOGGER.debug("Search home GET request");
		QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);

		List<RoomType> roomTypes = Database.listRoomTypes();
		roomTypes.add(new RoomType());
		model.addAttribute("roomTypes", roomTypes);
		model.addAttribute("rooms", Database.listRooms());
		model.addAttribute("token", token);
		model.addAttribute("config", CONFIG);
	    model.addAttribute("counter", new Counter());
		token.addToHttpServletResponse(response);
		return "searchRoom";
	}

	@PostMapping("/search")
	public String httpPostSearchPage(
			@CookieValue(value = QR_TRAVEL_ADMIN_TOKEN, defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
			HttpServletResponse response, Model model, @Valid SearchRoomForm searchRoomForm,
			BindingResult bindingResult) {
		LOGGER.debug("Search home POST request");
		QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);

		List<Room> roomList = null;

		List<RoomType> roomTypes = Database.listRoomTypes();
		roomTypes.add(new RoomType());
		
		if (bindingResult.hasErrors()) {
			LOGGER.error("bind result has errors");
			List<FieldError> fel = bindingResult.getFieldErrors();
			for (FieldError fe : fel) {
				LOGGER.trace(fe.toString());
			}
		} else {
			if ((searchRoomForm.getRoomType() != 0 ) && !("".equals(searchRoomForm.getDescription())))  {
				roomList = Database.searchRoomsByTypeAndDescription(Database.searchRoomTypeById(searchRoomForm.getRoomType()), searchRoomForm.getDescription());
			} else {
				if (searchRoomForm.getRoomType() != 0 ) {
					roomList = Database.searchRoomsByType(Database.searchRoomTypeById(searchRoomForm.getRoomType()));
				} else {
					if (!("".equals(searchRoomForm.getDescription())) ) {
						roomList = Database.searchRoomsByDescription(searchRoomForm.getDescription());
					} else {
						roomList = Database.listRooms();
					}
				}
			}

		}
		LOGGER.debug(searchRoomForm.toString());

		searchRoomForm.setRoomType(searchRoomForm.getRoomType());
		searchRoomForm.setDescription(searchRoomForm.getDescription());
		model.addAttribute("roomTypes", roomTypes);
		model.addAttribute("rooms", roomList);
		model.addAttribute("token", token);
		model.addAttribute("config", CONFIG);
	    model.addAttribute("counter", new Counter());
		token.addToHttpServletResponse(response);
		return "searchRoom";
	}

}
