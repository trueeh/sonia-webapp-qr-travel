package sonia.commons.crypt.cipher;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 * @since 1.1.0
 */
public interface SimpleCipher
{

  /**
   * Method description
   *
   *
   * @param value
   *
   * @return
   */
  public String decrypt(String value);

  /**
   * Method description
   *
   *
   * @param value
   *
   * @return
   */
  public String encrypt(String value);
}
