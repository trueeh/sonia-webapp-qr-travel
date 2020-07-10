package sonia.webapp.qrtravel;

//~--- non-JDK imports --------------------------------------------------------
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import sonia.commons.crypt.util.HEX;

/**
 * Class description
 *
 *
 * @version $version$, 18/08/19
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>Dr. Thorsten Ludewig <t.ludewig@gmail.com>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
  private final static String CONFIG_RESOURCENAME = "/" + CONFIG_NAME;

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

  /**
   * Field description
   */
  private static Config config;

  //~--- static initializers --------------------------------------------------

  //~--- methods --------------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param configFile
   *
   * @return
   *
   * @throws IOException
   */
  public static Config readConfig(File configFile) throws IOException
  {
    LOGGER.info("reading config file:" + configFile.getAbsolutePath());

    if (configFile.exists() && configFile.canRead())
    {
      config = OBJECT_MAPPER.readValue(configFile, Config.class);
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
   *
   * @throws IOException
   */
  public static void writeConfig(File configFile) throws IOException
  {
    OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(configFile,
      config);
  }

  /**
   * Method description
   *
   *
   *
   * @throws IOException
   */
  public static void writeConfig() throws IOException
  {
    writeConfig(getDefaultConfigFile());
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

  /**
   * Method description
   *
   *
   * @return
   */
  public static synchronized Config getInstance()
  {
    if (config == null)
    {
      APP_HOME = System.getProperty("app.home");
      config = new Config();
      try
      {
        readConfig();
      }
      catch (IOException ex)
      {
        LOGGER.error("Reading config ", ex);
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
    String fileName = CONFIG_NAME;
    String resourceFileName = CONFIG_NAME;

    URL resourceUrl = Config.class.getResource(CONFIG_RESOURCENAME);

    if (resourceUrl != null)
    {
      resourceFileName = resourceUrl.getFile();
    }

    if (!Strings.isNullOrEmpty(APP_HOME))
    {
      fileName = APP_HOME + File.separator + CONFIG_DIRECTORY_NAME
        + File.separator + CONFIG_NAME;
    }
    else if (!Strings.isNullOrEmpty(resourceFileName))
    {
      fileName = resourceFileName;
    }

    LOGGER.debug("config filename = " + fileName);

    return new File(fileName);
  }

  public static void writeSampleConfig() throws IOException
  {
    Random random = new Random(System.currentTimeMillis());
    byte[] key = new byte[20];
    random.nextBytes(key);
    config = new Config();
    config.cipherKey = HEX.convert(key).toLowerCase();
    config.tokenTimeout = 60 * 60 * 24 * 365; // timeout in s == 8h
    config.webServicePort = 8080;
    config.webServiceUrl = "https://qr.yourdomain.de";
    config.ldapHostName = "ldap.yourdomain.de";
    config.ldapHostPort = 636;
    config.ldapHostSSL = true;
    config.ldapBaseDn = "dc=yourdomain,dc=de";
    config.ldapBindDn = "cn=qrreader,ou=Special Users,dc=yourdomain,dc=de";
    config.ldapBindPassword = "<not set>";
    config.ldapSearchScope = "SUB";
    config.ldapSearchAttribute = "mail";
    config.ldapSearchFilter = "(mail={0})";
    config.dbDriverClassName = "org.postgresql.Driver";
    config.dbUrl = "jdbc:postgresql://localhost:5432/qr";
    config.dbUser = "qr";
    config.dbPassword = "<not set>";
    config.enableCheckExpired = true;
    config.checkExpiredCron = "0 5 0 * * ?";
    config.expirationTimeInDays = 21;
    config.maxLoginAttempts = 3;
    config.loginFailedBlockingDuration = 180;
    Config.writeConfig();
    System.out.println(config.toString());
  }

  //~--- fields ---------------------------------------------------------------
  @Getter
  private String cipherKey;

  @Getter
  private int tokenTimeout;

  @Getter
  private String dbUrl;

  @Getter
  private String dbDriverClassName;

  @Getter
  private String dbUser;

  @Getter
  @JsonSerialize(using = PasswordSerializer.class)
  @JsonDeserialize(using = PasswordDeserializer.class)
  private String dbPassword;

  @Getter
  private String webServiceUrl;

  @Getter
  private int webServicePort;

  @Getter
  private String ldapHostName;

  @Getter
  private int ldapHostPort;

  @Getter
  private boolean ldapHostSSL;

  @Getter
  private String ldapBindDn;

  @Getter
  @JsonSerialize(using = PasswordSerializer.class)
  @JsonDeserialize(using = PasswordDeserializer.class)
  private String ldapBindPassword;

  @Getter
  private String ldapBaseDn;

  @Getter
  private String ldapSearchFilter;

  @Getter
  private String ldapSearchAttribute;

  @Getter
  private String ldapSearchScope;

  @Getter
  private String checkExpiredCron;

  @Getter
  private boolean enableCheckExpired;

  @Getter
  private long expirationTimeInDays;

  @Getter
  private int maxLoginAttempts;

  @Getter
  private int loginFailedBlockingDuration;
}
