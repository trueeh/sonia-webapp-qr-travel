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
public class Random extends java.util.Random
{

  /** Field description */
  private final static Random singleton = new Random();

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  private Random()
  {
    super(System.currentTimeMillis());
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param numberOfDigits
   *
   * @return
   */
  public static String getDigits(int numberOfDigits)
  {
    String str = null;

    if (numberOfDigits > 0)
    {
      str = "";

      for (int i = 0; i < numberOfDigits; i++)
      {
        str += singleton.nextInt(10);
      }
    }

    return str;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public static Random getRandom()
  {
    return singleton;
  }
}
