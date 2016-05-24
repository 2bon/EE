package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.io.IOException;

/**
 * Provides quick access to a disk-based hash table created by
 * a {@link DiskHashWriter}.
 *
 * @author Rick Li
 */
public class DiskHashReader 
{
  /** Size of the header we expect to find */
  static final int headerSize = 12;

  /** SubStore to read the hash from */
  private SubStoreReader subfile;

  /** Number of hash slots in the subfile */
  private int nSlots;

  /** Size of each hash slot */
  private int slotSize;

  /** Buffer used to read hash slot bytes */
  private byte[] slotBytes;

  /** Used to decode hash slot values */
  private PackedByteBuf slotBuf;

  /**
   * Read in the header of of the hash from the given subfile.
   *
   * @param subfile   Must have been created by DiskHashWriter.outputTo()
   */
  public DiskHashReader(SubStoreReader subfile)
    throws IOException 
  {
    this.subfile = subfile;

    // Read the header.
    byte[] magic = new byte[4];
    subfile.read(magic);
    if (magic[0] != 'h' ||
        magic[1] != 'a' ||
        magic[2] != 's' ||
        magic[3] != 'h')
      throw new IOException("SubStore isn't a proper DiskHash");

    nSlots = subfile.readInt();
    slotSize = subfile.readInt();

    // Allocate the slot buffer.
    slotBytes = new byte[slotSize];
    slotBuf = new PackedByteBuf(slotBytes);
  } // constructor

  /**
   * Closes the reader (and its associated subfile).
   */
  public void close() 
  {
    try {
      subfile.close();
    }
    catch (Exception e) {
    }
    subfile = null;
  } // close()

  /**
   * Locate the entry for the given string key. If not found, returns null.
   * @param key   key to look for
   */
  public PackedByteBuf find(String key)
    throws IOException 
  {
    // Don't allow empty string as a key, since it's used to mark
    // the end of a slot.
    //
    if (key.length() == 0)
      key = " ";

    // Find the location of the slot data. If zero, we can fail now.
    int slotNum = (key.hashCode() & 0xffffff) % nSlots;
    subfile.seek(headerSize + (slotNum * 4));
    int slotOffset = subfile.readInt();
    if (slotOffset == 0)
      return null;
    assert (slotOffset + slotSize) <= subfile.length() : "Corrupt hash offset";

    // Read the slot data (may be too much, but will always be enough).
    subfile.seek(slotOffset);
    subfile.read(slotBytes);
    slotBuf.setBytes(slotBytes);

    // Now scan the entries
    while (true) 
    {
      // Get the name. If empty, give up.
      String name = slotBuf.readString();
      if (name.length() == 0)
        return null;

      // Does it match? If not, advance to the next slot.
      if (!name.equals(key)) {
        slotBuf.skipBuffer();
        continue;
      }

      // Got a match!
      return slotBuf.readBuffer();
    } // while
  } // find()
} // class DiskHashReader
