package sonia.webapp.qrtravel;

import com.unboundid.ldap.sdk.LDAPException;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QrTravelApplication
{
  public static void main(String[] args) throws LDAPException
  {
    SpringApplication.run(QrTravelApplication.class, args);
  }
}
