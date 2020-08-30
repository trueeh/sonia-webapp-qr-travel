package sonia.webapp.qrtravel.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@ToString
public class LoginForm 
{
  @NotNull
  @Getter
  @Setter
  @Size( min = 2, message = "Minimum 2 Zeichen" )
  private String userId;
  
  @NotNull
  @Getter
  @Setter
  @Size( min = 6, message = "Minimum 6 Zeichen" )
  private String password;
}
