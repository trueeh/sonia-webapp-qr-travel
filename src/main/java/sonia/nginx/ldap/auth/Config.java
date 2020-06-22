package sonia.nginx.ldap.auth;

//~--- non-JDK imports --------------------------------------------------------
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.ToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Class description
 *
 *
 * @version $version$, 18/08/19
 * @author Dr. Thorsten Ludewig <t.ludewig@gmail.com>
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
  private static final String APP_HOME;

  /**
   * Field description
   */
  private static Config config;

  //~--- static initializers --------------------------------------------------
  static
  {
    APP_HOME = System.getProperty("app.home");

    try
    {
      config = new Config();
      readConfig();
    } catch (IOException ex)
    {
      LOGGER.error("Can't read configuration file", ex);
    }
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
   * @throws IOException
   */
  public static Config readConfig(File configFile) throws IOException
  {
    LOGGER.info("reading config file:" + configFile.getAbsolutePath());

    if ( configFile.exists() && configFile.canRead())
    {
      config = OBJECT_MAPPER.readValue(configFile, Config.class);
    }
    
    LOGGER.info( config.toString() );
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
  public static Config getInstance()
  {
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
    
    if ( resourceUrl != null )
    {
      resourceFileName = resourceUrl.getFile();
    }
    
    if (!Strings.isNullOrEmpty(APP_HOME))
    {
      fileName = APP_HOME + File.separator + CONFIG_DIRECTORY_NAME + File.separator + CONFIG_NAME;
    } else if (!Strings.isNullOrEmpty(resourceFileName))
    {
      fileName = resourceFileName;
    }

    return new File(fileName);
  }

  public static void main( String args[] ) throws Exception
  {
    Config.getInstance();
    config.tokenTimeout = 60 * 60 * 8; // timeout in s == 8h
    config.authServiceUri = "/auth-service";
    config.authServicePort = 8080;
    config.homeUrl = "http://localhost:8800";
    config.ldapHostName = "opendj";
    config.ldapHostPort = 1389;
    config.ldapHostSSL = false;
    config.ldapBaseDn = "ou=people,dc=example,dc=com";
    config.ldapBindDn = "cn=Directory Manager";
    config.ldapBindPassword = "password";
    config.ldapSearchScope = "ONE";
    config.ldapSearchAttribute = "uid";
    config.ldapSearchFilter = "(uid={0})";
    Config.writeConfig();
  }
  
  //~--- fields ---------------------------------------------------------------

  @Getter
  private int tokenTimeout;
  
  @Getter
  private String authServiceUri;
  
  @Getter
  private int authServicePort;
  
  @Getter
  private String homeUrl;

  @Getter
  private String ldapHostName;
  
  @Getter
  private int ldapHostPort;
  
  @Getter
  private boolean ldapHostSSL;
  
  @Getter
  private String ldapBindDn;
  
  @Getter
  private String ldapBindPassword;

  @Getter
  private String ldapBaseDn;

  @Getter
  private String ldapSearchFilter;

  @Getter
  private String ldapSearchAttribute;

  @Getter
  private String ldapSearchScope;  
}
