package sonia.webapp.qrtravel.controller.admin;

import com.opencsv.CSVWriter;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sonia.webapp.qrtravel.db.Attendee;
import sonia.webapp.qrtravel.db.Database;
import sonia.webapp.qrtravel.db.Room;
import sonia.webapp.qrtravel.ldap.LdapUtil;

@Controller
public class CsvExportController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    CsvExportController.class.getName());

  @GetMapping(path = "/admin/csvexport",
              produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void httpGetCsvExport(
    @RequestParam(name = "p", required = false) String pin,
    HttpServletResponse response)
  {
    LOGGER.debug("pin = " + pin);

    Room room = Database.findRoom(pin);

    response.setHeader("Content-Disposition", "attachment; filename=r" + room.
      getPin() + ".csv");

    try
    {
      String[] header = new String[]
      {
        "#", "Nachname", "Vorname", "E-Mail", "Telefon", "Matrikelnummer",
        "Platz", "Strasse", "Ort", "Kommen", "Gehen"
      };
      CSVWriter writer = new CSVWriter(response.getWriter(), ';', '"', '\\',
        "\r\n");
      writer.writeNext(header);
      int counter = 0;
      for (Attendee a : room.getAttendees())
      {
        String[] row = new String[]
        {
          Integer.toString(++counter),
          a.getSurname(), a.getGivenname(), a.getEmail(), a.getPhonenumber(),
          a.getStudentnumber(), a.getLocation(), a.getStreet(), a.getCity(),
          a.getArrive(), a.getDeparture()
        };

        writer.writeNext(row);
      }

      String[] blank = new String[]
      {
      };
      writer.writeNext(blank);

      String[] row = new String[]
      {
        " ", "PIN", "Bezeichnung"
      };
      writer.writeNext(row);
      row = new String[]
      {
        " ", "R" + room.getPin(), room.getDescription()
      };
      writer.writeNext(row);

      writer.close();
    }
    catch (IOException ex)
    {
      LOGGER.error("CSV writer");
    }
  }

  @GetMapping(path = "/admin/csvexport-rooms",
              produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public void httpGetCsvExportRooms(
    HttpServletResponse response)
  {
    List<Room> rooms = Database.listRooms();

    response.setHeader("Content-Disposition", "attachment; filename=QR-Raumuebersicht.csv");

    try
    {
      String[] header = new String[]
      {
        "#", "PIN", "Bezeichnung", "Typ", "Ort", "Domain"
      };

      CSVWriter writer = new CSVWriter(response.getWriter(), ';', '"', '\\',
        "\r\n");
      writer.writeNext(header);
      int counter = 0;

      for (Room r : rooms)
      {

        String[] row = new String[]
        {
          Integer.toString(++counter),
          r.getPin(), r.getDescription(), r.getRoomType().getDescription(), 
          LdapUtil.getLocalityFromUid(r.getOwnerUid()), r.getDomain()
        };

        writer.writeNext(row);
      }

      writer.close();
    }
    catch (IOException ex)
    {
      LOGGER.error("CSV writer");
    }

  }
}
