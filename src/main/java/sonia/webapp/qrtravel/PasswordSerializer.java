package sonia.webapp.qrtravel;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class PasswordSerializer extends JsonSerializer<String>
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    PasswordSerializer.class.getName());

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
    generator.writeString(Cipher.encrypt(value));
  }
}
