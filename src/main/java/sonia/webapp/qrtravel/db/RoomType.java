/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.db;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author th
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
  private int rtype;
  
  @Getter
  private String description;  
}
