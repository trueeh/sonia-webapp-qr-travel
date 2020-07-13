/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author th
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ApiRequest
{
  @Getter
  private String authToken;

  @Getter
  private String phone;

  @Getter
  private String pin;

  @Getter
  private String location;

  @Getter
  private String username;

  @Getter
  private String password;
}
