/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author th
 */
public class CheckExpiredJob implements Job
{
  private final static Logger LOGGER = LoggerFactory.getLogger(CheckExpiredJob.class.getName());
  
  @Override
  public void execute(JobExecutionContext jec) throws JobExecutionException
  {
    LOGGER.info( "Check expired job started" );
  } 
}
