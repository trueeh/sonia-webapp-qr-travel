package sonia.webapp.qrtravel;

import com.unboundid.ldap.sdk.LDAPException;
import java.util.Timer;
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
    Config config = Config.getInstance();
    Database.initialize();
    BuildProperties build = BuildProperties.getInstance();
    LOGGER.info("Project Name    : " + build.getProjectName());
    LOGGER.info("Project Version : " + build.getProjectVersion());
    LOGGER.info("Build Timestamp : " + build.getTimestamp());

    if (config.isEnableCheckExpired())
    {
      CheckExpiredTask task = new CheckExpiredTask();
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(task, 0,
        (config.getCheckExpiredInterval() > 0) ? config.
        getCheckExpiredInterval()
        * 1000 : 10000);
    }

    SpringApplication.run(QrTravelApplication.class, args);
  }
}
