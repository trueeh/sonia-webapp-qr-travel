/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.nginx.ldap.auth;

import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import static sonia.nginx.ldap.auth.AuthController.AUTH_SERVICE_TOKEN;
import static sonia.nginx.ldap.auth.AuthController.UNAUTHORIZED;

@Controller
public class LoginController
{
  private final static Config CONFIG = Config.getInstance();
  private final static Logger LOGGER = LoggerFactory.getLogger(
    LoginController.class.getName());
  
  @GetMapping("/login")
  public String login(@RequestHeader(name = "x-original-uri", required = false,
                                      defaultValue = "") String originalUri,
    @CookieValue(value = AUTH_SERVICE_TOKEN,
                 defaultValue = UNAUTHORIZED) String tokenValue,
    HttpServletResponse response, Model model)
  {
    System.out.println(" - get login - x-original-uri=" + originalUri);
    AuthToken token = AuthToken.fromCookieValue(tokenValue, originalUri);
    AuthController.addTokenToHttpServletResponse(token, response);
    model.addAttribute("counter", token.getLoginCounter() + 1);
    model.addAttribute("authServiceUri", CONFIG.getAuthServiceUri());
    return "login";
  }

}
