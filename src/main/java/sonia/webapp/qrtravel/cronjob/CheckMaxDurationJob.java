package sonia.webapp.qrtravel.cronjob;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.Config;
import sonia.webapp.qrtravel.db.Database;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class CheckMaxDurationJob implements Job
{
  private final static Logger LOGGER = LoggerFactory.getLogger(CheckMaxDurationJob.class.getName());

  private final static Config CONFIG = Config.getInstance();

  private final static long MILLIS_PER_MINUTE = 1000 * 60;

  @Override
  public void execute(JobExecutionContext jec) throws JobExecutionException
  {
    long maxDurationTimestamp = System.currentTimeMillis() - (CONFIG.getMaxDurationInMinutes() * MILLIS_PER_MINUTE);
    LOGGER.debug("Check max duration job started");
    LOGGER.debug("Current time millis = {}, expirationTimestamp={}", System.currentTimeMillis(),
      maxDurationTimestamp);
    Database.departureMaxDurationAttendeeEntries(maxDurationTimestamp);
  }
}
