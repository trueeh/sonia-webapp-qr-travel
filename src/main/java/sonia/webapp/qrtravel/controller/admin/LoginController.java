package sonia.webapp.qrtravel.controller.admin;

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
import sonia.webapp.qrtravel.QrTravelAdminToken;
import static sonia.webapp.qrtravel.QrTravelAdminToken.QR_TRAVEL_ADMIN_TOKEN;
import static sonia.webapp.qrtravel.QrTravelAdminToken.UNKNOWN_ADMIN_TOKEN;
import sonia.webapp.qrtravel.form.LoginForm;

/**
 *
 * @author Dr. Thorsten Ludewig <t.ludewig@gmail.com>
 */
@Controller
public class LoginController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    LoginController.class.getName());

  @GetMapping("/sys/login")
  public String httpGetLoginPage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, LoginForm loginForm)
  {
    String page = "login";
    LOGGER.debug("Login GET Request");
    QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);
    if( token.isAuthenticated() )
    {
      page = "redirect:/admin";
    }
    model.addAttribute("token", token);
    token.addToHttpServletResponse(response);
    return page;
  }

  @GetMapping("/sys/logout")
  public String httpGetLogoutPage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, LoginForm loginForm)
  {
    LOGGER.debug("Login GET Request");
    QrTravelAdminToken token = QrTravelAdminToken.fromCookieValue(tokenValue);
    token.setAuthenticated(false);
    model.addAttribute("token", token);
    token.addToHttpServletResponse(response);
    return "redirect:/sys/login";
  }

  @PostMapping("/sys/login")
  public String httpPostLoginPage(
    @CookieValue(value = QR_TRAVEL_ADMIN_TOKEN,
                 defaultValue = UNKNOWN_ADMIN_TOKEN) String tokenValue,
    HttpServletResponse response, Model model, @Valid LoginForm loginForm,
    BindingResult bindingResult)
  {
    String page = "login";
    
    LOGGER.debug("Login POST Request");
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
      LOGGER.trace("user id = {}", loginForm.getUserId());
      LOGGER.trace("password = {}", loginForm.getPassword());
      
      token.setAuthenticated(true);
      page = "redirect:/admin";
    }

    model.addAttribute("token", token);
    token.addToHttpServletResponse(response);
    return page;
  }
}
