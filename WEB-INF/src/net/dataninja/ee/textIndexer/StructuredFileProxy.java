package net.dataninja.ee.textIndexer;


/*
net.dataninja copyright statement
 */
import java.io.File;
import java.io.IOException;
import net.dataninja.ee.util.Path;
import net.dataninja.ee.util.StructuredFile;
import net.dataninja.ee.util.StructuredStore;
import net.dataninja.ee.util.SubStoreReader;
import net.dataninja.ee.util.SubStoreWriter;

/*
 * This file created on Mar 22, 2005 by Rick Li
 */

/**
 * Used to put off actually creating a structured store until it is needed.
 * Essentially, all methods are delegated to a StructuredFile that is created
 * when the first time a method is called. The file is released after a
 * close() or delete() operation. As an additional precaution, the store is
 * created under a temporary file name, and only renamed to the final filename
 * when the file is closed.
 *
 * @author Rick Li
 */
public class StructuredFileProxy implements StructuredStore 
{
  private File finalPath;
  private File tmpPath;
  private StructuredFile realStore = null;

  public StructuredFileProxy(File path) {
    this.finalPath = path;
    this.tmpPath = new File(path.toString() + ".tmp");
  }

  public SubStoreWriter createSubStore(String name)
    throws IOException 
  {
    return realStore().createSubStore(name);
  }

  public SubStoreReader openSubStore(String name)
    throws IOException 
  {
    return realStore().openSubStore(name);
  }

  public void close()
    throws IOException 
  {
    if (realStore != null)
      realStore.close();
    if (tmpPath != null && tmpPath.canRead()) {
      if (!tmpPath.renameTo(finalPath))
        throw new IOException("Error renaming temporary store to final: " + finalPath);
    }
    realStore = null;
    finalPath = tmpPath = null;
  }

  public void delete()
    throws IOException 
  {
    if (realStore != null)
      realStore.delete();
    if (tmpPath.canRead())
      tmpPath.delete();
    realStore = null;
    finalPath = tmpPath = null;
  }

  public String getSystemId() {
    return realStore().getSystemId();
  }

  public void setUserVersion(String ver)
    throws IOException 
  {
    realStore().setUserVersion(ver);
  }

  public String getUserVersion() {
    return realStore().getUserVersion();
  }

  private StructuredFile realStore() 
  {
    try 
    {
      if (realStore == null) {
        if (finalPath.canRead())
          finalPath.delete();
        if (tmpPath.canRead())
          tmpPath.delete();
        Path.createPath(finalPath.getParent());
        realStore = StructuredFile.create(tmpPath);
      }
      return realStore;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
} // class StructuredFileProxy
