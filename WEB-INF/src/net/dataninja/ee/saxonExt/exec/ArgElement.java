package net.dataninja.ee.saxonExt.exec;


/*
net.dataninja copyright statement
 *
 * Acknowledgements:
 *
 * A significant amount of new and/or modified code in this module
 * was made possible by a grant from the Andrew W. Mellon Foundation,
 * as part of the Melvyl Recommender Project.
 */

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.RoleLocator;
import net.sf.saxon.expr.TypeChecker;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.instruct.GeneralVariable;
import net.sf.saxon.instruct.InstructionDetails;
import net.sf.saxon.instruct.TailCall;
import net.sf.saxon.om.*;
import net.sf.saxon.style.XSLGeneralVariable;
import net.sf.saxon.trace.InstructionInfo;
import net.sf.saxon.trace.Location;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

/**
 * Represents an &lt;arg> element below a &lt;run> instruction.
 *
 * @author Rick Li
 */
public class ArgElement extends XSLGeneralVariable 
{
  /**
  * Determine whether this node is an instruction.
  * @return false - it is not an instruction
  */
  public boolean isInstruction() {
    return false;
  }

  /**
  * Determine whether this type of element is allowed to contain a template-body
  * @return false: no, it may not contain a template-body
  */
  public boolean mayContainSequenceConstructor() {
    return false;
  }

  public void prepareAttributes()
    throws XPathException 
  {
    getVariableFingerprint();

    AttributeCollection atts = getAttributeList();

    String selectAtt = null;

    for (int a = 0; a < atts.getLength(); a++) 
    {
      String localName = atts.getLocalName(a);
      if (localName.equals("select")) {
        selectAtt = atts.getValue(a);
      }
      else {
        checkUnknownAttribute(atts.getNameCode(a));
      }
    }

    if (selectAtt != null) {
      select = makeExpression(selectAtt);
    }
  } // prepareAttributes()

  public void validate()
    throws XPathException 
  {
    if (!(getParent() instanceof RunElement)) {
      compileError("parent node must be exec:run");
    }
    if (select != null) 
    {
      select = typeCheck("select", select);
      try {
        RoleLocator role = new RoleLocator(RoleLocator.INSTRUCTION,
                                           "exec:run/arg",
                                           0,
                                           null);
        select = TypeChecker.staticTypeCheck(select,
                                             SequenceType.SINGLE_ATOMIC,
                                             false,
                                             role,
                                             getStaticContext());
      }
      catch (XPathException err) {
        compileError(err);
      }
    }
  } // validate()

  public Expression compile(Executable exec)
    throws XPathException 
  {
    ArgInstruction inst = new ArgInstruction();
    initializeInstruction(exec, inst);
    return inst;
  }

  protected static class ArgInstruction extends GeneralVariable 
  {
    public ArgInstruction() {
    }

    public InstructionInfo getInstructionInfo() {
      InstructionDetails details = (InstructionDetails)super.getInstructionInfo();
      details.setConstructType(Location.EXTENSION_INSTRUCTION);
      return details;
    }

    public TailCall processLeavingTail(XPathContext context) {
      return null;
    }

    /**
     * Evaluate the variable (method exists only to satisfy the interface)
     */
    public ValueRepresentation evaluateVariable(XPathContext context)
      throws XPathException 
    {
      throw new UnsupportedOperationException();
    }
  }
} // class ArgInstruction
