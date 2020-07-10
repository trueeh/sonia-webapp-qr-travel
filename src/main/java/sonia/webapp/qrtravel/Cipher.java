/**
 * OSTFALIA CONFIDENTIAL
 *
 * 2010 - 2019 Ostfalia University of Applied Sciences All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Ostfalia University of Applied Sciences and its suppliers, if any. The
 * intellectual and technical concepts contained herein are proprietary to
 * Ostfalia University of Applied Sciences and its suppliers and may be covered
 * by U.S. and Foreign Patents, patents in process, and are protected by trade
 * secret or copyright law. Dissemination of this information or reproduction of
 * this material is strictly forbidden unless prior written permission is
 * obtained from Ostfalia University of Applied Sciences.
 */

package sonia.webapp.qrtravel;
 
import sonia.commons.crypt.cipher.AesSimpleCipher;
 
/**
 *
 * @author your name
 */
public final class Cipher 
{
   
  /** secret key */
  private static final char[] KEY = Config.getInstance().getCipherKey().toCharArray();
  
  public static void main(String[] args)
  {
    System.out.println( KEY );
  }

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