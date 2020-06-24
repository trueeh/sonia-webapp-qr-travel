/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.ldap;

import com.unboundid.ldap.sdk.Entry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author th
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
