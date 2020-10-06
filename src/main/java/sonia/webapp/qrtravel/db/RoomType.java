package sonia.webapp.qrtravel.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
@Entity(name = "roomtype")
@NamedQueries(
{
  @NamedQuery(name = "listRoomTypes", query = "select a, upper(a.description) as orderName from roomtype a order by orderName")
})
@ToString
public class RoomType implements Serializable
{
  @Id
  @Getter
  private @JsonIgnore int rtype;
  
  @Getter
  private String description;  

  @Getter
  @Column(unique=true, nullable=true, name = "external_id")
  private @JsonIgnore
  Long externalId;

}
