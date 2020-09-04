package sonia.webapp.qrtravel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class PasswordDeserializer extends JsonDeserializer<String>
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    PasswordDeserializer.class.getName());
  
  @Override
  public String deserialize(JsonParser parser, DeserializationContext context)
    throws IOException, JsonProcessingException
  {
    String value = parser.getText();
    LOGGER.debug( value );
    return Cipher.decrypt(value);
  }
}
