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



package sonia.commons.crypt.cipher;

//~--- JDK imports ------------------------------------------------------------

import sonia.commons.crypt.converter.ByteConverter;
import sonia.commons.crypt.converter.HexByteConverter;
import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 * @since 1.1.0
 */
public final class AesSimpleCipher implements SimpleCipher
{

  /** Field description */
  private static final String CHARSET = "UTF-8";

  /** Field description */
  private static final String CIPHER_ALGORITHM = "AES";

  /** Field description */
  private static final int DEFAULT_KEYITERATIONS = 1024;

  /** Field description */
  private static final int DEFAULT_SALTLENGTH = 8;

  /** Field description */
  private static final String KEY_ALGORITHM = "AES";

  /** Field description */
  private static final String KEY_DIGEST = "SHA-1";

  /** Field description */
  private static final int MAX_KEYSALT = 12;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param key
   */
  public AesSimpleCipher(char[] key)
  {
    this(new HexByteConverter(), new SecureRandom(), DEFAULT_KEYITERATIONS,
      null, key, DEFAULT_SALTLENGTH);
  }

  /**
   * Constructs ...
   *
   *
   * @param converter
   * @param random
   * @param keyIterations
   * @param keySalt
   * @param key
   * @param saltLength
   */
  private AesSimpleCipher(ByteConverter converter, Random random,
    int keyIterations, byte[] keySalt, char[] key, int saltLength)
  {
    if (converter == null)
    {
      this.converter = new HexByteConverter();
    }
    else
    {
      this.converter = converter;
    }

    if (random == null)
    {
      this.random = new SecureRandom();
    }
    else
    {
      this.random = random;
    }

    this.saltLength = saltLength;

    if (keySalt == null)
    {
      try
      {
        keySalt = AesSimpleCipher.class.getSimpleName().getBytes(CHARSET);

        if (keySalt.length > 8)
        {
          keySalt = Arrays.copyOf(keySalt, 8);
        }
      }
      catch (UnsupportedEncodingException ex)
      {
        throw new CipherRuntimeException("could not find default encoding", ex);
      }
    }
    else if (keySalt.length > MAX_KEYSALT)
    {
      throw new CipherRuntimeException(
        "keySalt exceeds maximum length of ".concat(
          String.valueOf(MAX_KEYSALT)));
    }

    SecretKey secretKey = createSecretKey(keySalt, key, keyIterations);

    try
    {
      encrypter = Cipher.getInstance(CIPHER_ALGORITHM);

      encrypter.init(Cipher.ENCRYPT_MODE, secretKey);
    }
    catch (Exception ex)
    {
      throw new CipherRuntimeException("could not initialize encrpter", ex);
    }

    try
    {
      decrypter = Cipher.getInstance(CIPHER_ALGORITHM);

      decrypter.init(Cipher.DECRYPT_MODE, secretKey);
    }
    catch (Exception ex)
    {
      throw new CipherRuntimeException("could not initialize encrpter", ex);
    }
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public static Builder builder()
  {
    return new Builder();
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
  public String decrypt(String value)
  {
    try
    {
      byte[] encodedInput = converter.decode(value);
      byte[] salt = new byte[saltLength];
      byte[] encoded = new byte[encodedInput.length - saltLength];

      System.arraycopy(encodedInput, 0, salt, 0, saltLength);
      System.arraycopy(encodedInput, saltLength, encoded, 0,
        encodedInput.length - saltLength);

      byte[] decoded = decrypter.doFinal(encoded);

      return new String(decoded, CHARSET);
    }
    catch (Exception ex)
    {
      throw new CipherRuntimeException("could not decrypt value", ex);
    }
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
  public String encrypt(String value)
  {
    String encrypted;

    try
    {
      byte[] salt = new byte[saltLength];

      random.nextBytes(salt);

      byte[] inputBytes = value.getBytes(CHARSET);
      byte[] encodedInput = encrypter.doFinal(inputBytes);
      byte[] result = new byte[salt.length + encodedInput.length];

      System.arraycopy(salt, 0, result, 0, saltLength);
      System.arraycopy(encodedInput, 0, result, saltLength,
        result.length - saltLength);

      encrypted = converter.encode(result);
    }
    catch (Exception ex)
    {
      throw new CipherRuntimeException("could not encrypt value", ex);
    }

    return encrypted;
  }

  /**
   * Method description
   *
   *
   *
   * @param salt
   * @param keyChars
   * @param iterations
   *
   * @return
   */
  private SecretKey createSecretKey(byte[] salt, char[] keyChars,
    int iterations)
  {
    SecretKey key;

    try
    {
      MessageDigest digest = MessageDigest.getInstance(KEY_DIGEST);
      byte[] keyBytes = new String(keyChars).getBytes(CHARSET);
      byte[] saltedKey = new byte[salt.length + keyBytes.length];

      System.arraycopy(salt, 0, saltedKey, 0, salt.length);
      System.arraycopy(keyBytes, 0, saltedKey, salt.length, keyBytes.length);

      for (int i = 0; i < iterations; i++)
      {
        saltedKey = digest.digest(saltedKey);
      }

      byte[] digestKey = new byte[16];

      Arrays.fill(digestKey, (byte) 0xff);

      System.arraycopy(saltedKey, 0, digestKey, 0,
        Math.min(saltedKey.length, digestKey.length));

      key = new SecretKeySpec(digestKey, KEY_ALGORITHM);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new CipherRuntimeException("could not find key algorithm", ex);
    }
    catch (UnsupportedEncodingException ex)
    {
      throw new CipherRuntimeException("could decode key chars", ex);
    }

    return key;
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 13/07/10
   * @author Thorsten Ludewig <t.ludewig@ostfalia.de>        Enter your name here...
   */
  public static final class Builder
  {

    /**
     * Constructs ...
     *
     */
    private Builder() {}

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param key
     *
     * @return
     */
    public AesSimpleCipher build(char[] key)
    {
      return new AesSimpleCipher(converter, random, keyIterations, keySalt,
        key, saltLength);
    }

    /**
     * Method description
     *
     *
     * @param converter
     *
     * @return
     */
    public Builder converter(ByteConverter converter)
    {
      this.converter = converter;

      return this;
    }

    /**
     * Method description
     *
     *
     * @param keyIterations
     *
     * @return
     */
    public Builder keyIterations(int keyIterations)
    {
      this.keyIterations = keyIterations;

      return this;
    }

    /**
     * Method description
     *
     *
     * @param keySalt
     *
     * @return
     */
    public Builder keySalt(byte[] keySalt)
    {
      this.keySalt = keySalt;

      return this;
    }

    /**
     * Method description
     *
     *
     * @param random
     *
     * @return
     */
    public Builder random(Random random)
    {
      this.random = random;

      return this;
    }

    /**
     * Method description
     *
     *
     * @param saltLength
     *
     * @return
     */
    public Builder saltLength(int saltLength)
    {
      this.saltLength = saltLength;

      return this;
    }

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private ByteConverter converter;

    /** Field description */
    private int keyIterations = DEFAULT_KEYITERATIONS;

    /** Field description */
    private byte[] keySalt;

    /** Field description */
    private Random random;

    /** Field description */
    private int saltLength = DEFAULT_SALTLENGTH;
  }


  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private ByteConverter converter;

  /** Field description */
  private Cipher decrypter;

  /** Field description */
  private Cipher encrypter;

  /** Field description */
  private Random random;

  /** Field description */
  private int saltLength;
}
