/**
 * 
 */
package net.dataninja.ee.saxonExt;

/**
dataninja copyright statement
 */

import java.util.HashMap;
import java.util.Map;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.om.AttributeCollection;
import net.sf.saxon.om.Axis;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.XPathException;

/**
 * Base class that automates much of the tedious Saxon housekeeping for an
 * extension element that supports arbitrary content.
 * 
 * @author Rick Li
 */
public abstract class ElementWithContent extends ExtensionInstruction 
{
  protected Map<String, Expression> attribs = new HashMap();

  /**
   * Parse mandatory and optional attributes during prepareAttributes() call.
   */
  protected void parseAttributes(String[] mandatoryAtts, String[] optionalAtts) 
    throws XPathException
  {
    AttributeCollection inAtts = getAttributeList();
    for (int i = 0; i < inAtts.getLength(); i++) 
    {
      String attName = inAtts.getLocalName(i);
      for (String m : mandatoryAtts) {
        if (m.equalsIgnoreCase(attName))
          attribs.put(m, makeAttributeValueTemplate(inAtts.getValue(i)));
      }
      for (String m : optionalAtts) {
        if (m.equalsIgnoreCase(attName))
          attribs.put(m, makeAttributeValueTemplate(inAtts.getValue(i)));
      }
      
      // There may be other attributes present (e.g. xmlns:xxx, etc.) so don't complain
      // if there's no match.
    }
    
    // Make sure all manditory attributes were specified
    for (String m : mandatoryAtts) {
      if (!attribs.containsKey(m)) {
        reportAbsence(m);
        return;
      }
    }    
  }
  
  /**
   * Derived classes need to come up with their mandatory and optional
   * attributes and call parseAttributes() above.
   */
  public abstract void prepareAttributes() throws XPathException; 
  
  /**
   * Call during compile() to get the content expression.
   */
  protected Expression compileContent(Executable exec) 
    throws XPathException 
  {
    return compileSequenceConstructor(exec, iterateAxis(Axis.CHILD), true); 
  }
  
  /**
   * Determine whether this type of element is allowed to contain a template-body
   */
  public boolean mayContainSequenceConstructor() {
    return true;
  }
}