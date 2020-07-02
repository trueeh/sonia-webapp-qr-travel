/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sonia.webapp.qrtravel.BuildProperties;

@RestController
public class BuildController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(BuildController.class.getName());

  @GetMapping( path= "/build", produces = MediaType.APPLICATION_JSON_VALUE)
  public BuildProperties room()
  {
    LOGGER.debug("get build request");
    return BuildProperties.getInstance();
  }
}
