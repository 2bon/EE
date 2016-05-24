package net.dataninja.ee.lazyTree;

/**
dataninja copyright statement
 */

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.pattern.NodeTestPattern;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;

/**
 * Optimizes Saxon's NodeTestPattern adding the ability to directly use a
 * NodeTest when selecting elements, rather than selecting all elements
 * and then applying the test to them (the latter is slow on lazy trees).
 * 
 * @author Rick Li
 */
class FastNodeTestPattern extends NodeTestPattern
{
  public FastNodeTestPattern(NodeTest test) {
    super(test);
  }
  
  public SequenceIterator selectNodes(DocumentInfo doc, final XPathContext context) 
    throws XPathException 
  {
    if (getNodeKind() == Type.ELEMENT)
      return doc.iterateAxis(Axis.DESCENDANT, getNodeTest());
    return super.selectNodes(doc, context);
  }
}
