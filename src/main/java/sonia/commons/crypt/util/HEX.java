/**
 * OSTFALIA, COMPUTING CENTER CONFIDENTIAL
 *
 * 2000 - 2013 Ostfalia University of Applied Sciences, Computing Center
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Ostfalia University of Applied Sciences, Computing Center and its suppliers.
 * The intellectual and technical concepts contained herein are proprietary to
 * Ostfalia University of Applied Sciences, Computing Center. Dissemination of this
 * information or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from Ostfalia University of Applied Sciences,
 * Computing Center.
 */


package sonia.commons.crypt.util;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class HEX
{

  /** Field description */
  public final static char HEXCHARS[] =
  {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
    'F'
  };

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param b
   *
   * @return
   */
  public static String convert(byte b)
  {
    return "" + HEXCHARS[(b & 0xff) >> 4] + HEXCHARS[b & 0x0f];
  }

  /**
   * Method description
   *
   *
   * @param array
   *
   * @return
   */
  public static String convert(byte array[])
  {
    String returnValue = "";

    for (int i = 0; i < array.length; i++)
    {
      returnValue += (convert(array[i]));
    }

    return returnValue;
  }
}
