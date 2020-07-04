/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.controller;

import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sonia.webapp.qrtravel.BuildProperties;
import sonia.webapp.qrtravel.QrTravelToken;
import static sonia.webapp.qrtravel.QrTravelToken.QR_TRAVEL_TOKEN;
import static sonia.webapp.qrtravel.QrTravelToken.UNKNOWN_TOKEN;

@RestController
public class UtilController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(UtilController.class.getName());

  @GetMapping( path= "/build", produces = MediaType.APPLICATION_JSON_VALUE)
  public BuildProperties buildProperties()
  {
    LOGGER.debug("get build request");
    return BuildProperties.getInstance();
  }

  @GetMapping( path= "/acceptCookie", produces = MediaType.APPLICATION_JSON_VALUE)
  public HashMap<String,Boolean> acceptCookie(@CookieValue(value = QR_TRAVEL_TOKEN, defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response)
  {
    LOGGER.debug("accept cookie request");
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);
    token.setCookieAccepted(true);
    HashMap<String,Boolean> map = new HashMap<>();
    map.put("cookieAccepted", Boolean.TRUE );
    LOGGER.debug( token.toString() );
    token.addToHttpServletResponse(response);
    return map;
  }
}
