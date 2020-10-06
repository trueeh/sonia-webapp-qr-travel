package sonia.webapp.qrtravel;

import com.google.common.base.Strings;
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
    factory.setPort(CONFIG.getWebServicePort());
    
    if ( ! Strings.isNullOrEmpty(CONFIG.getContextPath()))
    {
      LOGGER.info( "set context path to: " + CONFIG.getContextPath());
      factory.setContextPath(CONFIG.getContextPath());
    }
    else
    {
      LOGGER.info( "set context path to root" );
      factory.setContextPath("");
    }
  }
}
