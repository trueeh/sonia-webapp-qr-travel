package sonia.webapp.qrtravel;
 
import sonia.commons.crypt.cipher.AesSimpleCipher;
 
/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>your name
 */
public final class Cipher 
{
   
  /** secret key */
  private static final char[] KEY = Config.getInstance().getCipherKey().toCharArray();
  
  /** cipher */
  private static final AesSimpleCipher CIPHER = 
    AesSimpleCipher.builder().build(KEY);

  /**
   * Encrypt an plaint text value.
   *
   * @param value value to decrypt
   *
   * @return encrypted value
   */   
  public static String encrypt(String value)
  {
    return CIPHER.encrypt(value);
  }

  /**
   * Decrypt an encrypted value.
   *
   * @param value value to encrypt
   *
   * @return decrypted value
   */
  public static String decrypt(String value)
  {
    return CIPHER.decrypt(value);
  }
}