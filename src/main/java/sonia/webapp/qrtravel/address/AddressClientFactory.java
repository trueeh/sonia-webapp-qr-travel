package sonia.webapp.qrtravel.address;

import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.Config;

/**
 *
 * @author Dr.-Ing. Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class AddressClientFactory
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    AddressClientFactory.class.getName());

  private final static Config CONFIG = Config.getInstance();

  public static AddressClient createAddressClient()
  {
    AddressClient instance = null;
    LOGGER.debug("Create Address Client with class name = {}", CONFIG.
      getAddressClientClassName());

    try
    {
      instance = (AddressClient) Class.forName(CONFIG.
        getAddressClientClassName()).getDeclaredConstructor(new Class[0]).
        newInstance(new Object[0]);
    }
    catch (ClassNotFoundException | NoSuchMethodException | SecurityException
      | InstantiationException | IllegalAccessException
      | IllegalArgumentException | InvocationTargetException ex)
    {
      LOGGER.error("Can't create address client", ex);
    }

    return instance;
  }
}
