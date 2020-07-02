/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.LoggerFactory;
import sonia.webapp.qrtravel.controller.ExamController;

/**
 *
 * @author th
 */
@ToString
public class BuildProperties
{
  
  /*
  build.java.version=@java.version@
build.java.vendor=@java.vendor@
build.project.name=@project.name@
build.project.version=@project.version@
build.timestamp=@build.timestamp@
  */
  
  private final static BuildProperties SINGLETON = new BuildProperties();
  private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    BuildProperties.class.getName());
  
  private BuildProperties()
  {
    Properties p = new Properties();
    InputStream is = this.getClass().getResourceAsStream("/build.properties");
    try
    {
      p.load(is);
      javaVersion = p.getProperty("build.java.version");
      javaVendor = p.getProperty("build.java.vendor");
      projectName = p.getProperty("build.project.name");
      projectVersion = p.getProperty("build.project.version");
      timestamp = p.getProperty("build.timestamp");
    }
    catch (IOException ex)
    {
      LOGGER.error( "Can't load properties. ", ex );
    }
  }
  
  public static BuildProperties getInstance()
  {
    return SINGLETON;
  }
  
  @Getter
  private String javaVersion;

  @Getter
  private String javaVendor;

  @Getter
  private String projectName;

  @Getter
  private String projectVersion;

  @Getter
  private String timestamp;
}
