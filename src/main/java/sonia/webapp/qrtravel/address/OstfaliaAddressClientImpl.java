package sonia.webapp.qrtravel.address;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.Config;

/**
 *
 * @author Dr.-Ing. Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class OstfaliaAddressClientImpl extends GenericAddressClientImpl
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    OstfaliaAddressClientImpl.class.getName());

  private final static Config CONFIG = Config.getInstance();

  @Override
  public List<Address> searchByCardNumber(String cardNumber)
  {
    LOGGER.debug("seraching address for cardnumber = {}", cardNumber );
    
    //TODO: implement search
    
    return new ArrayList<>();
  }
}
