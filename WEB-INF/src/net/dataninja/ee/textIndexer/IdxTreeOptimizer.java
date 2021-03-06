package net.dataninja.ee.textIndexer;


/**
net.dataninja copyright statement
 */
import java.io.File;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import net.dataninja.ee.textEngine.NativeFSDirectory;
import net.dataninja.ee.util.Path;
import net.dataninja.ee.util.Trace;

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

/**
 * This class provides a simple mechanism for optimizing Lucene indices
 * after new documents have been added , updated, or removed. <br><br>
 *
 * When documents are added to a Lucene index, they form a "segment" that
 * contains information about the location and frequency for words appearing
 * in the document. Optimizing a Lucene index consists of merging multiple
 * segments into a single large segment. Doing so speeds searching by
 * eliminating the need to search multiple segments and combine the results.
 * <br><br>
 *
 * To use this class, simply instantiate a copy, and call the
 * {@link IdxTreeOptimizer#processDir(File) processDir()}
 * method on a directory containing an index. Note that the directory passed
 * may also be a root directory with many index sub-directories if desired.
 */
public class IdxTreeOptimizer 
{
  ////////////////////////////////////////////////////////////////////////////

  /**
   * Create an <code>IdxTreeOptimizer</code> instance and call this method to
   * optimize one or more Lucene indices. <br><br>
   *
   * @param  dir         The index database directory optimize. May be a
   *                     directory containing a single index, or the root
   *                     directory of a tree containing multiple indices.
   *                     <br><br>
   *
   * @throws Exception   Passes back any exceptions generated by the
   *                     {@link IdxTreeOptimizer#optimizeIndex(File) optimizeIndex()}
   *                     function, which is called for each index sub-directory
   *                     found. <br><br>
   *
   * @.notes             This method also calls itself recursively to process
   *                     potential index sub-directories below the passed
   *                     directory.
   */
  public void processDir(File dir)
    throws Exception 
  {
    // If the file we were passed was in fact a directory...
    if (dir.getAbsoluteFile().isDirectory()) 
    {
      // And it contains an index, optimize it.
      if (IndexReader.indexExists(dir.getAbsoluteFile()))
        optimizeIndex(dir);

      else 
      {
        // Get the list of files it contains.
        String[] files = dir.getAbsoluteFile().list();

        // And process each of them.
        for (int i = 0; i < files.length; i++)
          processDir(new File(dir, files[i]));
      }

      return;
    } // if( dir.isDirectory() )

    // The current file is not a directory, so skip it.
  } // processDir()

  ////////////////////////////////////////////////////////////////////////////

  /**
   * Performs the actual work of optimizing a Lucene index.
   * <br><br>
   *
   * @param  idxDirToOptimize   The index database directory clean. This
   *                            directory must contain a single Lucene index.
   *                            <br><br>
   *
   * @throws Exception          Passes back any exceptions generated by Lucene
   *                            during the opening or optimization of the
   *                            specified index.
   *                            <br><br>
   */
  public void optimizeIndex(File idxDirToOptimize)
    throws Exception 
  {
    // Tell what index we're working on...
    String path = Path.normalizePath(idxDirToOptimize.toString());
    Trace.info("Index: [" + path + "] ... ");
    Trace.tab();

    try 
    {
      // Try to open the index for writing. If we fail and 
      // throw, skip the index.
      //
      Directory dir = NativeFSDirectory.getDirectory(idxDirToOptimize);
      IndexWriter indexWriter = new IndexWriter(dir, new StandardAnalyzer(), false);

      // Previously we were paranoid about using compound files, on the
      // mistaken assumption that indexes could not be modified. This is
      // not true... the modifications simply take place at the next merge,
      // which is always the case in Lucene (compound or not.)
      //
      // Thus, do not do the following:
      // NO NO NO: indexWriter.setUseCompoundFile( false );

      // Optimize the index.
      indexWriter.optimize();

      // Close the index.
      indexWriter.close();

      // Indicate that we're done.
      Trace.more(Trace.info, "Done.");
    } //  try( to open the specified index )

    catch (Exception e) {
      Trace.error("*** Optimization Halted Due to Error:" + e);
      throw e;
    }

    Trace.untab();
  } // optimizeIndex()
}
