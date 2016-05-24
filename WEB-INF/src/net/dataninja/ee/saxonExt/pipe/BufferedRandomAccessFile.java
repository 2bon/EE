package net.dataninja.ee.saxonExt.pipe;

/*
dataninja copyright statement
 */

import java.io.IOException;
import java.io.RandomAccessFile;

import com.lowagie.text.pdf.RandomAccessFileOrArray;

/**
 * Class to provide buffered, random access to a PDF file. Useful for when we
 * can't realistically fit a PDF file into memory.
 * 
 * @author Rick Li
 */
class BufferedRandomAccessFile extends RandomAccessFileOrArray
{
  // Unbuffered base file
  RandomAccessFile baseFile;
  
  // Support for pushing back a single byte
  byte prevByte;
  boolean havePrevByte = false;
  
  // Buffering
  final int BUFFER_SIZE = 4096;
  byte[] buffer = new byte[BUFFER_SIZE];
  int startOffset = 0;
  int bufferLength = 0;
  int bufferPos = 0;
  int bufferFilePointer = 0;
  String filename;

  /* Constructor - open up the file and initialize the file pointer to zero */
  public BufferedRandomAccessFile(String filename) throws IOException {
    super(filename, false, true);
    this.filename = filename;
    baseFile = new RandomAccessFile(filename, "r");
  }

  @Override
  public void pushBack(byte b) {
    prevByte = b;
    havePrevByte = true;
  }
  
  /**
   * Fill our buffer with data at the current file pointer.
   */
  private void fillBuffer() throws IOException 
  {
    bufferFilePointer = (int) baseFile.getFilePointer();
    bufferLength = baseFile.read(buffer);
    bufferPos = 0;
  }
  
  @Override
  public int read() throws IOException 
  {
    if (havePrevByte) {
      havePrevByte = false;
      return prevByte & 0xff;
    }
    if (bufferPos >= bufferLength) {
      fillBuffer();
      if (bufferPos >= bufferLength)
        return -1;
    }
    return buffer[bufferPos++] & 0xff;
  }
  
  @Override
  public int read(byte[] b, int off, int len) throws IOException 
  {
    int origLen = len;
    
    // If there was a pushback, use it.
    if (havePrevByte && len > 0) {
      havePrevByte = false;
      b[off] = prevByte;
      ++off;
      --len;
    }
    
    // Copy as much as we can from the buffer.
    int toCopy = Math.min(bufferLength - bufferPos, len);
    if (toCopy > 0) {
      System.arraycopy(buffer, bufferPos, b, off, toCopy);
      bufferPos += toCopy;
      off += toCopy;
      len -= toCopy;
    }
    
    // For anything remaining, get it straight from the file.
    if (len > 0) {
      int nRead = baseFile.read(b, off, len);
      off += nRead;
      len -= nRead;
    }
    
    // And let the caller know how much we were able to read.
    return origLen - len;
  }
  
  @Override
  public int skipBytes(int n) throws IOException 
  {
    int origN = n;

    // Eat the 'back' byte
    if (havePrevByte && n > 0) {
      havePrevByte = false;
      --n;
    }
    
    // Skip in the buffer if we can
    int nBufSkip = Math.min(bufferLength - bufferPos, n);
    if (nBufSkip > 0) {
      bufferPos += nBufSkip;
      n -= nBufSkip;
    }
    
    // Skip by seeking if we must
    if (n > 0) {
      baseFile.seek(baseFile.getFilePointer() + n);
      n = 0;
    }
    
    return Math.max(0, origN - n);
  }
  
  @Override
  public void reOpen() throws IOException {
    if (filename != null && baseFile == null)
      baseFile = new RandomAccessFile(filename, "r");
    seek(0);
  }

  @Override
  protected void insureOpen() throws IOException {
    if (filename != null && baseFile == null) {
        reOpen();
    }
  }
  
  @Override
  public boolean isOpen() {
    return (baseFile != null);
  }
  
  @Override
  public void close() throws IOException {
    havePrevByte = false;
    if (baseFile != null) {
      baseFile.close();
      baseFile = null;
    }
    super.close();
  }
  
  @Override
  public void setStartOffset(int off) {
    startOffset = off;
  }
  
  @Override
  public int getStartOffset() {
    return startOffset;
  }
  
  @Override
  public int length() throws IOException {
    insureOpen();
    return (int) baseFile.length() - startOffset;
  }
  
  @Override
  public void seek(int pos) throws IOException 
  {
    insureOpen();
    havePrevByte = false;
    if (pos >= bufferFilePointer && (pos - bufferFilePointer) < bufferLength) {
      bufferPos = pos - bufferFilePointer;
      return;
    }
    baseFile.seek(pos + startOffset);
    bufferLength = bufferPos = 0;
    bufferFilePointer = pos;
  }
  
  @Override
  public void seek(long pos) throws IOException {
    seek((int)pos);
  }
  
  @Override
  public int getFilePointer() throws IOException {
    insureOpen();
    return bufferFilePointer + bufferPos - startOffset - (havePrevByte ? 1 : 0);
  }
  
  @Override
  public java.nio.ByteBuffer getNioByteBuffer() throws IOException { 
    throw new RuntimeException("Not supported");
  }
}