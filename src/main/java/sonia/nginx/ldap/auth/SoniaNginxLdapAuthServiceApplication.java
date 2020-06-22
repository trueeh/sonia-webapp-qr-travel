package sonia.nginx.ldap.auth;

import com.unboundid.ldap.sdk.LDAPException;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoniaNginxLdapAuthServiceApplication
{
  public static void main(String[] args) throws LDAPException
  {
    SpringApplication.run(SoniaNginxLdapAuthServiceApplication.class, args);
  }
}
