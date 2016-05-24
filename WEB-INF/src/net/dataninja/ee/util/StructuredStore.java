package net.dataninja.ee.util;


/*
net.dataninja copyright statement
 */

/*
 * This file created on Mar 11, 2005 by Rick Li
 */
import java.io.IOException;

/**
 * A simple structured storage with a flat top-level directory.
 * Substores can be added to an existing storage using {@link #createSubStore(String)},
 * and accessed later using {@link #openSubStore(String)}.
 *
 * @author Rick Li
 */
public interface StructuredStore 
{
  /**
   * Create a new sub-store with the specified name. Returns a SubStore that
   * has most of the interface of a RandomAccessFile, except that seeks
   * will be relative to the sub-store start.
   *
   * Only one substore may be created at a time (though many others may be
   * opened, provided they were created before.)
   *
   * The caller must call SubStore.close() when the file is complete, to
   * ensure that the directory gets written.
   *
   * @param name  Name of the sub-file to create. Must not exist.
   * @return      A sub-store to write to.
   */
  SubStoreWriter createSubStore(String name)
    throws IOException;

  /**
   * Opens a pre-existing sub-store for read (or write). Returns a sub-store that
   * has most of the interface of a RandomAccessFile, except that seeks will
   * be relative to the sub-file start, and IO operations cannot exceed the
   * boundaries of the sub-file.
   *
   * Many sub-stores may be open simultaneously; each one has an independent
   * file pointer. Each one is light weight, so it's okay to have many open
   * at a time.
   *
   * @param name  Name of pre-existing sub-store to open.
   */
  SubStoreReader openSubStore(String name)
    throws IOException;

  /** Gets the path, URI, or other unique identifier for this store */
  String getSystemId();

  /**
   * Sets a user-defined version number for the file. It can be retrieved
   * later with {@link #getUserVersion()}.
   *
   * @param ver   The version number to set.
   */
  void setUserVersion(String ver)
    throws IOException;

  /**
   * Gets the user version (if any) set by {@link #setUserVersion(String)}.
   */
  String getUserVersion();

  /**
   * Closes the store. This should always be called, to ensure that all
   * sub-stores have been closed and that the directory has been written.
   */
  void close()
    throws IOException;

  /** Deletes the storage completely (implies close() first) */
  void delete()
    throws IOException;
}
