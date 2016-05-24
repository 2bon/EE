package net.dataninja.ee.lazyTree;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import java.nio.CharBuffer;
import net.dataninja.ee.util.PackedByteBuf;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;

/**
 * A simple text node, which knows how to load its text from the persistent
 * disk file.
 *
 * @author Rick Li
 */
class TextImpl extends NodeImpl 
{
  protected String text;

  /** Initialize the text node, and load the text. */
  public void init(int textOffset, int textLen)
    throws IOException 
  {
    if (textLen > 0) {
      byte[] bytes = new byte[textLen];
      document.textFile.seek(textOffset);
      document.textFile.read(bytes);
      PackedByteBuf buf = new PackedByteBuf(bytes);
      text = buf.readString();
    }
  }

  /**
   * Return the character value of the node.
   * @return the string value of the node
   */
  public String getStringValue() {
    return text;
  }

  /**
   * Return the type of node.
   * @return Type.TEXT
   */
  public final int getNodeKind() {
    return Type.TEXT;
  }

  /**
   * Copy this node to a given outputter
   */
  public void copy(Receiver out, int whichNamespaces, boolean copyAnnotations,
                   int locationId)
    throws XPathException 
  {
    out.characters(CharBuffer.wrap(text), locationId, 0);
  }
} // class TextImpl
