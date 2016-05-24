package net.dataninja.ee.saxonExt.pipe;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.ListIterator;

/*
dataninja copyright statement
 */

/**
 * Keeps a pool of buffers used by the Pipe saxon extension functions, to
 * minimize per-request memory gobbling.
 */
class PipeBufferPool
{
  static final int MAX_SPARE_BUFS = 4;
  static final int BUF_SIZE = 32*1024; // 32 Kbytes
  static LinkedList spareBuffers = new LinkedList();

  /**
   * Allocate a buffer to use for I/O. Uses previously allocated buffer if 
   * possible (that buffer must have been deallocated using deallocBuffer()).
   */
  static synchronized byte[] allocBuffer()
  {
    byte[] buf = null;
    
    // Look for a previous buffer we can use.
    ListIterator iter = spareBuffers.listIterator();
    while (iter.hasNext() && buf == null)
    {
      Object obj = iter.next();
      iter.remove();
      
      // If it's a weak reference, the buffer might still be around.
      if (obj instanceof WeakReference) 
      {
        WeakReference<byte[]> ref = (WeakReference<byte[]>)obj;
        buf = ref.get();
      }
      else
        buf = (byte[]) obj;
    }

    // If no buffers available to re-use, create a new one.
    if (buf == null)
      buf = new byte[BUF_SIZE];
    
    // All done.
    return buf;
  }
  
  /**
   * Return a buffer so it can be re-used later. If we already have enough
   * spare buffers then make it a weak reference so the buffer can be 
   * garbage-collected.
   */
  static synchronized void deallocBuffer(byte[] buf)
  {
    // Remove buffers which got garbage-collected from the list.
    ListIterator iter = spareBuffers.listIterator();
    while (iter.hasNext()) {
      Object obj = iter.next();
      if (obj instanceof WeakReference && ((WeakReference)obj).get() == null)
        iter.remove();
    }
      
    // If we could use another permanent buffer, keep forever.
    if (spareBuffers.size() < MAX_SPARE_BUFS)
      spareBuffers.addFirst(buf);
    else 
    {
      // Otherwise make a weak reference so the buffer can be garbage
      // collected. There's still the chance that we'll get to re-use
      // it.
      //
      spareBuffers.addFirst(new WeakReference(buf));
    }
  }
}
