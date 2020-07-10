package sonia.webapp.qrtravel;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.commons.crypt.cipher.AesSimpleCipher;

/**
 *
 * @author th
 */
public class PasswordSerializer extends JsonSerializer<String>
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    PasswordSerializer.class.getName());

    /** secret key */
  private static final char[] KEY = new char[]
  {
    '2', 'f', '7', 'a', '3', '9', 'c', '2', 
    'a', '7', 'd', '7', 'f', 'f', '8', '9', 
    'f', '0', 'e', 'f', '6', '1', 'e', 'a', 
    '2', '4', '0', 'a', 'b', '9', '4', '4', 
    'd', '1', '5', 'd', 'f', '0', 'f', 'f'
  };
   
  /** cipher */
  private static final AesSimpleCipher CIPHER = 
    AesSimpleCipher.builder().build(KEY);

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
  
  /**
   *
   * @param value
   * @param generator
   * @param provider
   * @throws IOException
   */
  @Override
  public void serialize(String value, JsonGenerator generator, SerializerProvider provider)
    throws IOException
  {
    LOGGER.debug( value );
    generator.writeString(encrypt(value));
  }
}
