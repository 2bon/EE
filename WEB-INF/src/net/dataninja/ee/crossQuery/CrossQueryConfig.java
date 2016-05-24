package net.dataninja.ee.crossQuery;


/**
net.dataninja copyright statement
 */
import net.dataninja.ee.servletBase.TextConfig;
import net.dataninja.ee.util.GeneralException;

/** Holds global configuration information for the crossQuery servlet. */
public class CrossQueryConfig extends TextConfig 
{
  /**
   * The stylesheet used route an HTTP request to the appropriate query
   * parser stylesheet.
   */
  public String queryRouterSheet;

  /**
   * The stylesheet used to parse a query from an HTTP request, into an XML
   * format usable by the text engine (only specified if no query router).
   */
  public String queryParserSheet;

  /** Default constructor */
  public CrossQueryConfig(CrossQuery servlet) {
    super(servlet);
  }

  /**
   * Constructor - Reads and parses the global configuration file (XML) for
   * the crossQuery servlet.
   *
   * @param  path               Filesystem path to the config file.
   * @throws GeneralException   If a read or parse error occurs.
   */
  public CrossQueryConfig(CrossQuery servlet, String path)
    throws GeneralException 
  {
    super(servlet);
    super.read("crossQuery-config", path);

    // Make sure required things were specified.
    if (queryRouterSheet == null || queryRouterSheet.length() == 0) {
      if (queryParserSheet == null || queryParserSheet.length() == 0)
        requireOrElse(queryRouterSheet,
                      "Config file error: queryRouter path not specified");
    }
  }

  /**
   * Called by when a property is encountered in the configuration file.
   * If we recognize the property we process it here; otherwise, we pass
   * it on to the base class for recognition there.
   */
  public boolean handleProperty(String tagAttr, String strVal) 
  {
    if (tagAttr.equalsIgnoreCase("queryRouter.path")) {
      queryRouterSheet = servlet.getRealPath(strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("queryParser.path")) {
      queryParserSheet = servlet.getRealPath(strVal);
      return true;
    }

    // Don't recognize it... see if the base class does.
    return super.handleProperty(tagAttr, strVal);
  } // handleProperty()
} // class CrossQueryConfig
