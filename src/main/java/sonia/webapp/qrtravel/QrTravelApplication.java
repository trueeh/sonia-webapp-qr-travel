package sonia.webapp.qrtravel;

import com.unboundid.ldap.sdk.LDAPException;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import sonia.webapp.qrtravel.db.Database;

@SpringBootApplication
public class QrTravelApplication
{
  public static void main(String[] args) throws LDAPException
  {
    Database.initialize();
    SpringApplication.run(QrTravelApplication.class, args);
  }
}
