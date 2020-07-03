package sonia.webapp.qrtravel;

import com.unboundid.ldap.sdk.LDAPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import sonia.webapp.qrtravel.db.Database;

@SpringBootApplication
public class QrTravelApplication
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    QrTravelApplication.class.getName());

  public static void main(String[] args) throws LDAPException
  {
    if (System.getProperty("app.home") == null)
    {
      System.setProperty("app.home", ".");
    }
    Config.getInstance();
    Database.initialize();
    BuildProperties build = BuildProperties.getInstance();
    LOGGER.info( "Project Name    : " + build.getProjectName() );
    LOGGER.info( "Project Version : " + build.getProjectVersion() );
    LOGGER.info( "Build Timestamp : " + build.getTimestamp() );
    SpringApplication.run(QrTravelApplication.class, args);
  }
}
