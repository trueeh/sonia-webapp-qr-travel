package sonia.webapp.qrtravel.ldap;

import com.unboundid.ldap.sdk.Entry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@ToString
public class LdapAccount
{
  public LdapAccount(Entry entry)
  {
    this.dn = entry.getDN();
    this.uid = entry.getAttributeValue("uid");
    this.sn = entry.getAttributeValue("sn");
    this.givenName = entry.getAttributeValue("givenName");
    this.mail = entry.getAttributeValue("mail");
    this.soniaStudentNumber = entry.getAttributeValue(
      "soniaStudentNumber");
  }

  @Getter
  private final String dn;

  @Getter
  @Setter
  private String mail;

  @Getter
  @Setter
  private String uid;

  @Getter
  @Setter
  private String sn;

  @Getter
  @Setter
  private String givenName;

  @Getter
  @Setter
  private String soniaStudentNumber;
}
