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
import java.util.ArrayList;
import java.util.List;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.XPathException;

/**
 * Implements a Saxon instruction that executes an external process and
 * properly formats the result. Provides timeout and error checking.
 *
 * @author Rick Li
 */
public class RunElement extends ExtensionInstruction 
{
  protected Expression command;
  protected int timeoutMsec;

  public void prepareAttributes()
    throws XPathException 
  {
    // Get mandatory 'command' attribute
    command = makeAttributeValueTemplate(getAttributeList().getValue("", "command"));
    if (command == null) {
      reportAbsence("command");
      return;
    }

    // Get optional 'timeout' attribute
    String timeoutStr = getAttributeList().getValue("", "timeout");
    if (timeoutStr == null)
      timeoutMsec = 0;
    else 
    {
      try {
        timeoutMsec = (int)(Float.parseFloat(timeoutStr) * 1000);
        timeoutMsec = Math.max(0, timeoutMsec);
      }
      catch (NumberFormatException e) {
        compileError("'timeout' must be a number");
      }
    }
  } // prepareAttributes()

  public Expression compile(Executable exec)
    throws XPathException 
  {
    return new RunInstruction(command, timeoutMsec, getArgInstructions(exec));
  }

  public List getArgInstructions(Executable exec)
    throws XPathException 
  {
    List list = new ArrayList(10);

    AxisIterator kids = iterateAxis(Axis.CHILD);
    NodeInfo child;
    while (true) 
    {
      child = (NodeInfo)kids.next();
      if (child == null)
        break;
      if (child instanceof ArgElement)
        list.add(((ArgElement)child).compile(exec));
      if (child instanceof InputElement) {
        list.add(((InputElement)child).compile(exec));
      }
    }

    return list;
  } // getArgInstructions()

} // class ExecInstruction
