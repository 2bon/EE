package net.dataninja.ee.zing;


/**
net.dataninja copyright statement
 */
import net.dataninja.ee.crossQuery.CrossQueryConfig;
import net.dataninja.ee.util.GeneralException;

/** Holds global configuration information for the SRU servlet. */
class SRUConfig extends CrossQueryConfig 
{
  /**
   * Constructor - Reads and parses the global configuration file (XML) for
   * the SRU servlet.
   *
   * @param  path               Filesystem path to the config file.
   * @throws GeneralException   If a read or parse error occurs.
   */
  public SRUConfig(SRU servlet, String path)
    throws GeneralException 
  {
    super(servlet);
    super.read("SRU-config", path);

    // Make sure required things were specified.
    requireOrElse(queryParserSheet,
                  "Config file error: queryParser path not specified");
  }
} // class SRUConfig
