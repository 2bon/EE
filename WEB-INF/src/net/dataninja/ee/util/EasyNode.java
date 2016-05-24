package net.dataninja.ee.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.AllElementStripper;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tinytree.TinyBuilder;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;

/*
dataninja copyright statement
 */

/**
 * Provides an easy-to-use wrapper around a Saxon NodeInfo.
 *
 * @author Rick Li
 */
public class EasyNode 
{
  /** The node we are wrapping */
  private NodeInfo wrapped;

  /** Attribute names for this node */
  private ArrayList<String> attrNames;

  /** Attribute values for this node */
  private ArrayList<String> attrValues;

  /** Child elements for this node */
  private ArrayList<EasyNode> children;
  
  /** Configuration used for parsing XML files */
  private static Configuration config = new Configuration();
  
  /**
   * Convenience method to read an XML file and return the root node.
   */
  public static EasyNode readXMLFile(String path) {
    return readXMLFile(new File(path));
  }
  
  /**
   * Convenience method to read an XML file and return the root node.
   */
  public static EasyNode readXMLFile(File path)
  {
    // Read in the document (it's in XML format)
    StreamSource src = new StreamSource(path);
    NodeInfo doc = null;
    try {
      doc = TinyBuilder.build(src, new AllElementStripper(), config);
      return new EasyNode(doc);
    }
    catch (XPathException e) {
      throw new RuntimeException(e);
    }
  }

  /** Wrap a NodeInfo */
  public EasyNode(NodeInfo toWrap) {
    wrapped = toWrap;
  }

  /**
   * Iterate the attributes and fill the 'attrNames' and 'attrValues' vectors.
   * This is a memo function and only has to do actual work once.
   */
  private void getAttrs() 
  {
    if (attrNames != null)
      return;

    attrNames = new ArrayList();
    attrValues = new ArrayList();

    NodeInfo attr;
    AxisIterator iter = wrapped.iterateAxis(Axis.ATTRIBUTE);
    while ((attr = (NodeInfo)iter.next()) != null) {
      String name = attr.getLocalPart();
      String val = attr.getStringValue();
      attrNames.add(name);
      attrValues.add(val);
    }
  } // getAttribs()

  /**
   * Iterate the children and fill the 'children' vector. This is a memo
   * function and only has to do actual work once.
   */
  private void getChildren() 
  {
    if (children != null)
      return;

    children = new ArrayList();

    NodeInfo child;
    AxisIterator iter = wrapped.iterateAxis(Axis.CHILD);
    while ((child = (NodeInfo)iter.next()) != null) 
    {
      if (child.getNodeKind() == Type.ELEMENT ||
          child.getNodeKind() == Type.TEXT) 
      {
        children.add(new EasyNode(child));
      }
    }
  } // getChildren()

  /** Get the number of attributes this node has */
  public int nAttrs() {
    getAttrs();
    return attrNames.size();
  } // nAttrs()

  /** Get a specific numbered attribute's name */
  public String attrName(int index) {
    getAttrs();
    return (String)attrNames.get(index);
  } // attrName()
  
  /** Get a list of all attribute names */
  public List<String> attrNames() {
    getAttrs();
    return attrNames;
  }

  /** Get a specific numbered attribute's value */
  public String attrValue(int index) {
    getAttrs();
    return (String)attrValues.get(index);
  } // attrName()

  /** Check if this node has the given attribute */
  public boolean hasAttr(String name) {
    return attrValue(name) != null;
  } // hasAttr()

  /** Get a named attribute's value, or null if no such name found. The
   *  name matching is performed case-insensitive. */
  public String attrValue(String name) 
  {
    getAttrs();
    for (int i = 0; i < attrNames.size(); i++) {
      if (((String)attrNames.get(i)).equalsIgnoreCase(name))
        return (String)attrValues.get(i);
    }
    return null;
  } // attrValue()

  /** Get a count of the number of children this node has */
  public int nChildren() {
    getChildren();
    return children.size();
  } // nChildren()

  /** Get a specific numbered child of this node */
  public EasyNode child(int index) {
    getChildren();
    return (EasyNode)children.get(index);
  }

  /** Get the first child node with the specified name, case insensitive.
   *  If no such child is found, returns null. */
  public EasyNode child(String name) 
  {
    getChildren();
    for (int i = 0; i < children.size(); i++) {
      if (((EasyNode)children.get(i)).name().equalsIgnoreCase(name))
        return (EasyNode)children.get(i);
    }
    return null;
  } // child()
  
  /** Get all the children */
  public List<EasyNode> children() {
    getChildren();
    return children;
  }

  /** Get the parent of this node (if any) */
  public EasyNode parent() {
    NodeInfo parent = wrapped.getParent();
    if (parent == null)
      return null;
    return new EasyNode(parent);
  } // parent()

  /** Get the name of this node */
  public String name() {
    return wrapped.getLocalPart();
  } // getName()

  /** Checks if this is an element node */
  public boolean isElement() {
    return wrapped.getNodeKind() == Type.ELEMENT;
  }

  /** Checks if this is a text node */
  public boolean isText() {
    return wrapped.getNodeKind() == Type.TEXT;
  }

  /** Get the string value of this node */
  public String toString() {
    return wrapped.getStringValue();
  }

  /** Get the actual node we're wrapping */
  public NodeInfo getWrappedNode() {
    return wrapped;
  } // getWrappedNode()
} // class EasyNode
