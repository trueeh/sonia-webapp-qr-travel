package sonia.webapp.qrtravel;

import java.io.IOException;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import sonia.webapp.qrtravel.db.Database;

@SpringBootApplication
public class QrTravelApplication
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    QrTravelApplication.class.getName());

  public static void main(String[] args) throws SchedulerException, IOException
  {
    if (System.getProperty("app.home") == null)
    {
      System.setProperty("app.home", ".");
    }

    if (args.length == 1 && args[0].equalsIgnoreCase("-w"))
    {
      Config.writeSampleConfig();
      System.exit(0);
    }

    if (args.length == 2 && args[0].equalsIgnoreCase("-e"))
    {
      System.out.println(args[1] + " = " + PasswordSerializer.encrypt(args[1]));
      System.exit(0);
    }

    if (args.length == 1 && args[0].equalsIgnoreCase("-c"))
    {
      System.out.println(Config.getInstance().toString());
      System.exit(0);
    }

    Config config = Config.getInstance();
    Database.initialize();

    BuildProperties build = BuildProperties.getInstance();
    LOGGER.info("Project Name    : " + build.getProjectName());
    LOGGER.info("Project Version : " + build.getProjectVersion());
    LOGGER.info("Build Timestamp : " + build.getTimestamp());

    if (config.isEnableCheckExpired())
    {
      SchedulerFactory schedulerFactory = new StdSchedulerFactory();
      Scheduler scheduler = schedulerFactory.getScheduler();
      scheduler.start();

      JobDetail job = JobBuilder.newJob(CheckExpiredJob.class)
        .withIdentity("CheckExpiredJob", "group1")
        .build();

      CronTrigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(CronScheduleBuilder.cronSchedule(config.
          getCheckExpiredCron()))
        .build();

      scheduler.scheduleJob(job, trigger);
    }

    SpringApplication.run(QrTravelApplication.class, args);
  }
}
