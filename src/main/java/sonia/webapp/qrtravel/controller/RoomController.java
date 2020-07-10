package sonia.webapp.qrtravel.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;

@RestController
public class RoomController
{
  @GetMapping( path= "/room", produces = MediaType.APPLICATION_JSON_VALUE)
  public Room room(
    @RequestParam(name = "p", required = true) String pin )
  {
    return Database.findRoom(pin);
  }
}
