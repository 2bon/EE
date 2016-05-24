package net.dataninja.ee.textIndexer;


/*
dataninja copyright statement
 *
 * Acknowledgements:
 *
 * A significant amount of new and/or modified code in this module
 * was made possible by a grant from the Andrew W. Mellon Foundation,
 * as part of the Melvyl Recommender Project.
 */
import java.io.IOException;
import org.xml.sax.InputSource;
import net.dataninja.ee.util.StructuredStore;

/**
 * A single record within a {@link IndexSource}. Note that many source files
 * have only one record, and that's okay.
 *
 * @author Rick Li
 */
public abstract class IndexRecord 
{
  /** Source to read XML data from */
  public abstract InputSource xmlSource()
    throws IOException;

  /** Numeric index of this record (zero if this is the only record */
  public abstract int recordNum();

  /**
   * Estimate of how much of the whole {@link IndexSource} will have been
   * completed when this record is complete.
   */
  public abstract int percentDone();

  /**
   * Empty storage in which to build the persistent version of the
   * record (aka the "lazy tree"), or null to avoid building it.
   */
  public abstract StructuredStore lazyStore();
} // class SrcRecord
