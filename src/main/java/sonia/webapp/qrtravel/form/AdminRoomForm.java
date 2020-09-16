package sonia.webapp.qrtravel.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sonia.webapp.qrtravel.QrTravelToken;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@ToString
public class AdminRoomForm 
{
  @NotNull
  @Size( min = 1, message = "Minimum 1 Zeichen" )  
  @Getter
  @Setter
  private String description;
    
  @Getter
  @Setter
  private int roomType;
}
