package net.dataninja.ee.textEngine;


/**
net.dataninja copyright statement
 */
import java.io.File;
import net.dataninja.ee.cache.GeneratingCache;
import net.dataninja.ee.textIndexer.IndexerConfig;
import net.dataninja.ee.textIndexer.XMLConfigParser;
import net.dataninja.ee.util.Path;

/**
 * Used to maintain a simple cache of config files, so we don't have to keep
 * loading the same one over and over.
 */
public class ConfigCache extends GeneratingCache 
{
  /**
   * Default constructor - defines the default cache size and expiration time.
   * These should probably be configurable in some file somewhere, but does
   * anyone really care?
   */
  public ConfigCache() {
    super(50, 300); // 50 entries, 300 seconds
  }

  /** Find or load the configuration given its File */
  public IndexerConfig find(File configFile, String indexName)
    throws Exception 
  {
    ConfigCacheKey key = new ConfigCacheKey();
    key.configPath = Path.normalizeFileName(configFile.toString());
    key.indexName = indexName;
    return (IndexerConfig)super.find(key);
  } // find()

  /** Load a configuration given its path */
  protected Object generate(Object key)
    throws Exception 
  {
    ConfigCacheKey realKey = (ConfigCacheKey)key;

    IndexerConfig config = new IndexerConfig();
    config.xtfHomePath = System.getProperty("ee.home");
    config.cfgFilePath = realKey.configPath;
    config.indexInfo.indexName = realKey.indexName;

    XMLConfigParser parser = new XMLConfigParser();
    parser.configure(config);

    return config;
  } // generate()

  /**
   * A key in the ConfigCache.
   */
  private static class ConfigCacheKey 
  {
    public String configPath;
    public String indexName;

    public int hashCode() {
      return configPath.hashCode() ^ indexName.hashCode();
    }

    public boolean equals(ConfigCacheKey other) {
      return configPath.equals(other.configPath) &&
             indexName.equals(other.indexName);
    }
  } // class ConfigCacheKey
}
