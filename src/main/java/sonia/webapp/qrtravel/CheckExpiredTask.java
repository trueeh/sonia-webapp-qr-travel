/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author th
 */
public class CheckExpiredTask extends TimerTask
{
  private final static Logger LOGGER = LoggerFactory.getLogger(CheckExpiredTask.class.getName());
  
  @Override
  public void run()
  {
    LOGGER.info( "Check expired task started" );
  }
  
}
