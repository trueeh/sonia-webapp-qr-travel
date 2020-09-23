package sonia.webapp.qrtravel;

//~--- non-JDK imports --------------------------------------------------------
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.google.common.base.Strings;
import java.io.BufferedReader;

import lombok.Getter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import sonia.commons.crypt.util.HEX;

/**
 * Class description
 *
 *
 * @author Dr. Thorsten Ludewig <t.ludewig@gmail.com>
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "cipherKey" })
@ToString
public class Config
{

  /**
   * Field description
   */
  private final static String CONFIG_NAME = "config.json";

  /**
   * Field description
   */
  private final static String CONFIG_DIRECTORY_NAME = "config";

  /**
   * Field description
   */
  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Field description
   */
  private final static Logger LOGGER = LoggerFactory.getLogger(
    Config.class.getName());

  /**
   * Field description
   */
  private static String APP_HOME;

  private static String cipher = null;

  /**
   * Field description
   */
  private static Config config;

  public Config()
  {
    Random random = new Random(System.currentTimeMillis());
    byte[] key = new byte[20];
    if (Strings.isNullOrEmpty(cipher))
    {
      random.nextBytes(key);
      cipherKey = HEX.convert(key).toLowerCase();
    }
    else
    {
      cipherKey = cipher;
    }
    random.nextBytes(key);
    apiAuthToken = HEX.convert(key).toLowerCase();
    tokenTimeout = 60 * 60 * 24 * 365; // timeout in s == 1Y
    adminTokenTimeout = 60 * 60; // timeout in s == 1h
    webServicePort = 8080;
    webServiceUrl = "https://qr.yourdomain.de";
    ldapHostName = "ldap.yourdomain.de";
    ldapHostPort = 636;
    ldapHostSSL = true;
    ldapBaseDn = "dc=yourdomain,dc=de";
    ldapBindDn = "cn=qrreader,ou=Special Users,dc=yourdomain,dc=de";
    ldapBindPassword = "<not set>";
    ldapSearchScope = "SUB";
    ldapSearchAttribute = "mail";
    ldapSearchFilter = "(mail={0})";
    dbDriverClassName = "org.postgresql.Driver";
    dbUrl = "jdbc:postgresql://localhost:5432/qr";
    dbUser = "qr";
    dbPassword = "<not set>";
    enableCheckExpired = true;
    checkExpiredCron = "0 5 0 * * ?";
    expirationTimeInDays = 21;
    
    enableCheckMaxDuration = true;
    checkMaxDurationCron = "0 0/5 * * * ?";
    maxDurationInMinutes = 12 * 60;
    
    maxLoginAttempts = 3;
    loginFailedBlockingDuration = 180;
    
    random.nextBytes(key);
    adminCipherKey = HEX.convert(key).toLowerCase();
    adminLdapSearchScope = "SUB";
    adminLdapSearchAttribute = "uid";
    adminLdapSearchFilter = "(&(uid={0})(isMemberOf=cn=qr-travel-admin,ou=groups,dc=yourdomain,dc=de))";
    
    influxDbForStatisticsEnabled = false;
    influxDbCron = "0 0/5 * * * ?";
    influxDbUrl = "https://influxdb.your.domain:8086/write?db=qr";
    influxDbUseAuthentication = true;
    influxDbUser = "qr";
    influxDbPassword = "<not set>";    
    domain = "your.domain";    
    contextPath = "/";
  }

  //~--- methods --------------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param configFile
   *
   * @return
   *
   */
  public static Config readConfig(File configFile)
  {
    LOGGER.info("reading config file:" + configFile.getAbsolutePath());

    if (configFile.exists() && configFile.canRead())
    {
      File cipherFile = new File(APP_HOME + File.separator
        + CONFIG_DIRECTORY_NAME + File.separator + "cipher.cfg");

      try
      {
        BufferedReader reader = new BufferedReader(new FileReader(cipherFile));
        cipher = reader.readLine();
        reader.close();
      }
      catch (IOException ex)
      {
        LOGGER.error("Can not read cipher file");
      }

      config.cipherKey = cipher;

      try
      {
        config = OBJECT_MAPPER.readValue(configFile, Config.class);
      }
      catch (IOException ex)
      {
        LOGGER.error("Can not read config file ", ex);
      }
    }

    LOGGER.info(config.toString());
    return config;
  }

  /**
   * Method description
   *
   *
   * @return
   *
   * @throws IOException
   */
  public static Config readConfig() throws IOException
  {
    return readConfig(getDefaultConfigFile());
  }

  /**
   * Method description
   *
   *
   * @param configFile
   * @param force *
   * @throws IOException
   */
  public static void writeConfig(File configFile, boolean force) throws
    IOException
  {
    if (!configFile.exists() || force)
    {
      File cipherFile = new File(APP_HOME + File.separator
        + CONFIG_DIRECTORY_NAME + File.separator + "cipher.cfg");

      if (!cipherFile.exists()) // never override cipher file
      {
        PrintWriter cipherWriter = new PrintWriter(cipherFile);
        cipherWriter.println(config.cipherKey);
        cipherWriter.close();
      }

      LOGGER.info("Writing config file: {}", configFile.getAbsolutePath());
      OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(configFile,
        config);
    }
    else
    {
      LOGGER.info("NOT overwriting config file: {}", configFile.
        getAbsolutePath());
    }
  }

  /**
   * Method description
   *
   *
   *
   * @throws IOException
   */
  public static void writeConfig(boolean force) throws IOException
  {
    writeConfig(getDefaultConfigFile(), force);
  }

  //~--- get methods ----------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param filename
   *
   * @return
   */
  public static String getAbsoluteVarPath(String filename)
  {
    return getVarDirectoryName() + File.separator + filename;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public static String getAppHome()
  {
    return APP_HOME;
  }

  public static synchronized Config getInstance()
  {
    return getInstance(true);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public static synchronized Config getInstance(boolean readConfig)
  {
    if (config == null)
    {
      config = new Config();

      APP_HOME = System.getProperty("app.home");

      if (Strings.isNullOrEmpty(APP_HOME))
      {
        APP_HOME = ".";
      }

      File configDirectory = new File(APP_HOME + File.separator
        + CONFIG_DIRECTORY_NAME);

      if (configDirectory.exists() && configDirectory.isDirectory()
        && configDirectory.canWrite() && configDirectory.canRead())
      {
        LOGGER.info("Config directory: " + configDirectory.getAbsolutePath());
      }
      else
      {
        LOGGER.info("Creating config directory: {}", configDirectory.
          getAbsolutePath());
        if (!configDirectory.mkdirs())
        {
          LOGGER.error("ERROR: Can't create config directory: "
            + configDirectory.
              getAbsolutePath());
          System.exit(-1);
        }
      }

      if (readConfig)
      {
        try
        {
          readConfig();
        }
        catch (IOException ex)
        {
          LOGGER.error("Reading config ", ex);
        }
      }
    }
    return config;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public static File getVarDirectory()
  {
    return new File(getVarDirectoryName());
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public static String getVarDirectoryName()
  {
    return APP_HOME + File.separator + "var";
  }

  /**
   * Method description
   *
   *
   * @param filename
   *
   * @return
   */
  public static File getVarFile(String filename)
  {
    return new File(getAbsoluteVarPath(filename));
  }

  /**
   * Method description
   *
   *
   * @return
   */
  private static File getDefaultConfigFile()
  {
    String filename = APP_HOME + File.separator + CONFIG_DIRECTORY_NAME
      + File.separator + CONFIG_NAME;

    File configFile = new File(filename);
    LOGGER.debug("config filename = " + configFile.getAbsolutePath());

    return configFile;
  }

  public static void writeSampleConfig(boolean force) throws IOException
  {
    Config.getInstance(false);
    Config.writeConfig(force);
    System.out.println(config.toString());
  }

  //~--- fields ---------------------------------------------------------------
  @Getter
  private String cipherKey;

  @Getter
  private final int tokenTimeout;

  @Getter
  private final String dbUrl;

  @Getter
  private final String dbDriverClassName;

  @Getter
  private final String dbUser;

  @Getter
  @JsonSerialize(using = PasswordSerializer.class)
  @JsonDeserialize(using = PasswordDeserializer.class)
  private final String dbPassword;

  @Getter
  private final String webServiceUrl;

  @Getter
  private final int webServicePort;

  @Getter
  private final String ldapHostName;

  @Getter
  private final int ldapHostPort;

  @Getter
  private final boolean ldapHostSSL;

  @Getter
  private final String ldapBindDn;

  @Getter
  @JsonSerialize(using = PasswordSerializer.class)
  @JsonDeserialize(using = PasswordDeserializer.class)
  private final String ldapBindPassword;

  @Getter
  private final String ldapBaseDn;

  @Getter
  private final String ldapSearchFilter;

  @Getter
  private final String ldapSearchAttribute;

  @Getter
  private final String ldapSearchScope;

  @Getter
  private final String checkExpiredCron;

  @Getter
  private final boolean enableCheckExpired;

  @Getter
  private final long expirationTimeInDays;
  
  
  @Getter
  private final String checkMaxDurationCron;

  @Getter
  private final boolean enableCheckMaxDuration;

  @Getter
  private final long maxDurationInMinutes;
  

  @Getter
  private final int maxLoginAttempts;

  @Getter
  private final int loginFailedBlockingDuration;

  @Getter
  private final String apiAuthToken;
  
  @Getter
  private final String adminCipherKey;
  
  @Getter
  private final String adminLdapSearchFilter;
  
  @Getter
  private final String adminLdapSearchAttribute;

  @Getter
  private final String adminLdapSearchScope;

  @Getter  
  private final int adminTokenTimeout;
  
  
  @Getter
  private final boolean influxDbForStatisticsEnabled;

  @Getter
  private final String influxDbCron;
  
  @Getter
  private final String influxDbUrl;
  
  @Getter
  private final boolean influxDbUseAuthentication;

  @Getter
  private final String influxDbUser;

  @Getter
  @JsonSerialize(using = PasswordSerializer.class)
  @JsonDeserialize(using = PasswordDeserializer.class)
  private final String influxDbPassword;
  
  @Getter
  private final String domain;
  
  @Getter
  private final String contextPath;

}
