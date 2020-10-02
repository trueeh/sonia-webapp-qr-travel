package sonia.webapp.qrtravel.address;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dr.-Ing. Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class GenericAddressClientImpl implements AddressClient
{
  @Override
  public List<Address> searchByUid(String uid)
  {
    return new ArrayList<>();
  }

  @Override
  public List<Address> searchByCardNumber(String cardNumber)
  {
    return new ArrayList<>();
  }
}
