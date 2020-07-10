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
public class ASCIIConverter
{

  /** Field description */
  public final static String[][] SPECIAL_CHARACTERS_MAPPING =
  {
    { "À", "A" }, { "Á", "A" }, { "Â", "A" }, { "Ã", "A" }, { "Ä", "Ae" },
    { "Å", "A" }, { "Æ", "Ae" }, { "Ç", "C" }, { "È", "E" }, { "É", "E" },
    { "Ê", "E" }, { "Ë", "E" }, { "Ì", "I" }, { "Í", "I" }, { "Î", "I" },
    { "Ï", "I" }, { "Ñ", "N" }, { "Ò", "O" }, { "Ó", "O" }, { "Ô", "O" },
    { "Õ", "O" }, { "Ö", "Oe" }, { "Ù", "U" }, { "Ú", "U" }, { "Û", "U" },
    { "Ü", "Ue" }, { "Ý", "Y" }, { "ß", "ss" }, { "à", "a" }, { "á", "a" },
    { "â", "a" }, { "ã", "a" }, { "ä", "ae" }, { "å", "a" }, { "æ", "a" },
    { "ç", "c" }, { "è", "e" }, { "é", "e" }, { "ê", "e" }, { "ë", "e" },
    { "ì", "i" }, { "í", "i" }, { "î", "i" }, { "ï", "i" }, { "ð", "o" },
    { "ñ", "n" }, { "ò", "o" }, { "ó", "o" }, { "ô", "o" }, { "õ", "o" },
    { "ö", "oe" }, { "ù", "u" }, { "ú", "u" }, { "û", "u" }, { "ü", "ue" },
    { "ý", "y" }, { "ÿ", "y" }
  };

  /** Field description */
  public final static String[] STANDARD_CHARACTERS =
  {
    "Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P", "A", "S", "D", "F", "G",
    "H", "J", "K", "L", "Y", "X", "C", "V", "B", "N", "M", "q", "w", "e", "r",
    "t", "z", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l",
    "y", "x", "c", "v", "b", "n", "m"
  };

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param str
   *
   * @return
   */
  public static String convert(String str)
  {
    return convert(str, false);
  }

  /**
   * Method description
   *
   *
   * @param str
   * @param removeUnknownChars
   * @param additionalKnownChars
   *
   * @return
   */
  public static String convert(String str, boolean removeUnknownChars,
    String... additionalKnownChars)
  {
    StringBuilder builder = new StringBuilder(str);

    if ((str != null) && (str.length() > 0))
    {

      // analyze each character in the given string
      for (int i = 0; i < builder.length(); i++)
      {
        boolean matched = false;
        String character = builder.substring(i, i + 1);

        // replace known special characters
        for (int j = 0; j < SPECIAL_CHARACTERS_MAPPING.length; j++)
        {
          if (character.equals(SPECIAL_CHARACTERS_MAPPING[j][0]))
          {
            builder.replace(i, i + 1, SPECIAL_CHARACTERS_MAPPING[j][1]);
            matched = true;
          }
        }

        // remove unknown special characters
        if (!matched && removeUnknownChars)
        {
          for (int j = 0; j < STANDARD_CHARACTERS.length; j++)
          {
            if (character.equals(STANDARD_CHARACTERS[j]))
            {
              matched = true;
            }
          }

          if (!matched && (additionalKnownChars != null))
          {
            for (int j = 0; j < additionalKnownChars.length; j++)
            {
              if (character.equals(additionalKnownChars[j]))
              {
                matched = true;
              }
            }
          }

          // unknown characters are "bad" characters
          if (!matched)
          {
            builder.deleteCharAt(i);
          }
        }
      }
    }

    return builder.toString();
  }
}
