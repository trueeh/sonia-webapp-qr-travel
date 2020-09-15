package sonia.webapp.qrtravel.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.Config;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class InfluxDbWriter
{
  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(
    InfluxDbWriter.class.getName());

  private InfluxDbWriter()
  {
  }

  public static void write(String pin, int numberOfAttendees,
    int currentAttendees, int departuredAttendees, int forcedDeparture,
    int averageDuration)
  {
    HttpURLConnection connection = null;

    String message = "room,pin=r" + pin
      + " numberOfAttendees=" + numberOfAttendees
      + ",currentAttendees=" + currentAttendees
      + ",departuredAttendees=" + departuredAttendees
      + ",forcedDeparture=" + forcedDeparture
      + ",averageDuration=" + averageDuration
      + "\n";

    try
    {
      URL connectionUrl = new URL(CONFIG.getInfluxDbUrl());
      connection = (HttpURLConnection) connectionUrl.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");

      if (CONFIG.isInfluxDbUseAuthentication())
      {
        String auth = CONFIG.getInfluxDbUser() + ":" + CONFIG.
          getInfluxDbPassword();
        String authHeaderValue = "Basic " + Base64.getEncoder().encodeToString(
          auth.getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", authHeaderValue);
      }

      LOGGER.debug(message);

      try (PrintWriter writer = new PrintWriter(connection.getOutputStream()))
      {
        writer.write(message);
      }

      StringBuilder content;

      try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(connection.getInputStream())))
      {
        String line;
        content = new StringBuilder();

        while ((line = reader.readLine()) != null)
        {
          content.append(line);
          content.append(System.lineSeparator());
        }
      }

      LOGGER.debug(content.toString());
      LOGGER.debug("response code : " + connection.getResponseCode());

    }
    catch (Exception e)
    {
      LOGGER.error("Writing to influxdb", e);
    }
    finally
    {
      if (connection != null)
      {
        connection.disconnect();
      }
    }
  }
}
