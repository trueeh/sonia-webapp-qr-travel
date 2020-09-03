/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import lombok.Getter;
import lombok.Setter;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>th
 */
public class Options
{

  @Setter
  @Getter
  @Option( name="--help", aliases="-h", usage="Display this help", required = false )
  private boolean help = false;
  
  
  @Getter
  @Option( name="--encrypt", aliases="-e", usage="Encrypt a password", required = false )
  private String encrypt;

  @Getter
  @Option( name="--create--sample-config", usage="Create a sample configuration file", required = false )
  private boolean createSampleConfig;

  @Getter
  @Option( name="--force", usage="Force overriding existing configuration file", required = false )
  private boolean force;

  @Getter
  @Option( name="--check-config", aliases="-c", usage="Check config file", required = false )
  private boolean checkConfig;
}
