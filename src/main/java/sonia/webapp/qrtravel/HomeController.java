/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import static sonia.webapp.qrtravel.QrTravelToken.QR_TRAVEL_TOKEN;
import static sonia.webapp.qrtravel.QrTravelToken.UNKNOWN_TOKEN;

@Controller
public class HomeController
{
  private final static Config CONFIG = Config.getInstance();
  private final static Logger LOGGER = LoggerFactory.getLogger(HomeController.class.getName());
  
  @GetMapping("/")
  public String home(@RequestHeader(name = "x-original-uri", required = false,
                                      defaultValue = "") String originalUri,
    @CookieValue(value = QR_TRAVEL_TOKEN,
                 defaultValue = UNKNOWN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model)
  {
    System.out.println(" - get login - x-original-uri=" + originalUri);
    
    QrTravelToken token = QrTravelToken.fromCookieValue(tokenValue);
    
    /*
    AuthToken token = AuthToken.fromCookieValue(tokenValue, originalUri);
    AuthController.addTokenToHttpServletResponse(token, response);
    model.addAttribute("counter", token.getLoginCounter() + 1);
    model.addAttribute("authServiceUri", CONFIG.getWebServiceUrl());
    */
    
    token.addToHttpServletResponse(response);
    return "home";
  }

}
