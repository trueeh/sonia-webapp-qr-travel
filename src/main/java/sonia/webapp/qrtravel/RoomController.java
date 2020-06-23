/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;

@RestController
public class RoomController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(RoomController.class.getName());

  @GetMapping( path= "/room", produces = MediaType.APPLICATION_JSON_VALUE)
  public Room room(
    @RequestParam(name = "p", required = true) String pin )
  {
    return Database.findRoom(pin);
  }
}
