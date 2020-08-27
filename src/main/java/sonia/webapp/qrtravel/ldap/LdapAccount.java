package sonia.webapp.qrtravel.ldap;

import com.unboundid.ldap.sdk.Entry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.codec.binary.Base64;

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
    this.soniaStudentNumber = entry.getAttributeValue("soniaStudentNumber");
    this.soniaChipcardBarcode = entry.getAttributeValue("soniaChipcardBarcode");
    this.jpegPhoto = null;

    // 
    this.ou = entry.getAttributeValue("ou");
    this.employeeType = entry.getAttributeValue("employeeType");

    // 
    byte[] imageData = entry.getAttributeValueBytes("jpegPhoto");
   
    if ((imageData != null) && (imageData.length > 0))
    {
      jpegPhoto = Base64.encodeBase64String(imageData);
    }
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

  @Getter
  @Setter
  private String soniaChipcardBarcode;

  @Getter
  @Setter
  private String ou;

  @Getter
  @Setter
  private String employeeType;

  @Getter
  @Setter
  @ToString.Exclude
  private String jpegPhoto;
}
