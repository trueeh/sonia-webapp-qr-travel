/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.nginx.ldap.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class WebServerCustomizer
  implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>
{
  
  private final static Logger LOGGER = LoggerFactory.getLogger(
    WebServerCustomizer.class.getName());
  
  private final static Config CONFIG = Config.getInstance();

  @Override
  public void customize(TomcatServletWebServerFactory factory)
  {
    LOGGER.info("WebServerCustomizer.customize");
    factory.setPort(CONFIG.getAuthServicePort());
  }
}
