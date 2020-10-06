package sonia.webapp.qrtravel.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sonia.webapp.qrtravel.QrTravelToken;

/**
 *
 * @author Hartmut Trüe
 */
@ToString
public class SearchRoomForm 
{
  @NotNull
  @Getter
  @Setter
  private String description;
    
  @Getter
  @Setter
  private int roomType;
}
