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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class QrViewController
{
  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(
    QrViewController.class.getName());

  @GetMapping("/view")
  public String qrPage(
    @RequestParam(name = "p", required = false) String pin,
    HttpServletResponse response, Model model)
  {
    LOGGER.info("pin = " + pin);
    model.addAttribute("pin", pin);
    return "view";
  }
}