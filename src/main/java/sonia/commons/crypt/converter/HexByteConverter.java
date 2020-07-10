/**
 * OSTFALIA CONFIDENTIAL
 *
 * 2010 - 2013 Ostfalia University of Applied Sciences All Rights Reserved.
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



package sonia.commons.crypt.converter;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 * @since 1.1.0
 */
public class HexByteConverter implements ByteConverter
{

  /** Field description */
  private static final char[] DIGITS =
  {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
    'f'
  };

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param ch
   * @param index
   *
   * @return
   *
   * @throws IllegalArgumentException
   */
  protected static int toDigit(char ch, int index)
    throws IllegalArgumentException
  {
    int digit = Character.digit(ch, 16);

    if (digit == -1)
    {
      throw new IllegalArgumentException("Illegal hexadecimal charcter " + ch
        + " at index " + index);
    }

    return digit;
  }

  /**
   * Method description
   *
   *
   * @param value
   *
   * @return
   */
  @Override
  public byte[] decode(String value)
  {
    char[] data = value.toCharArray();
    int len = data.length;

    if ((len & 0x01) != 0)
    {
      throw new IllegalArgumentException("Odd number of characters.");
    }

    byte[] out = new byte[len >> 1];

    // two characters form the hex value.
    for (int i = 0, j = 0; j < len; i++)
    {
      int f = toDigit(data[j], j) << 4;

      j++;
      f = f | toDigit(data[j], j);
      j++;
      out[i] = (byte) (f & 0xFF);
    }

    return out;
  }

  /**
   * Method description
   *
   *
   * @param data
   *
   * @return
   */
  @Override
  public String encode(byte[] data)
  {
    int l = data.length;

    char[] out = new char[l << 1];

    // two characters form the hex value.
    for (int i = 0, j = 0; i < l; i++)
    {
      out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
      out[j++] = DIGITS[0x0F & data[i]];
    }

    return new String(out);
  }
}
