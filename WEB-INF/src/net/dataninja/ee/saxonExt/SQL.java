package net.dataninja.ee.saxonExt;

import net.dataninja.ee.saxonExt.sql.SQLClose;
import net.dataninja.ee.saxonExt.sql.SQLColumn;
import net.dataninja.ee.saxonExt.sql.SQLConnect;
import net.dataninja.ee.saxonExt.sql.SQLDelete;
import net.dataninja.ee.saxonExt.sql.SQLInsert;
import net.dataninja.ee.saxonExt.sql.SQLProperty;
import net.dataninja.ee.saxonExt.sql.SQLQuery;
import net.dataninja.ee.saxonExt.sql.SQLUpdate;
import net.sf.saxon.style.ExtensionElementFactory;

/**
  * Class SQLElementFactory. <br>
  * A "Factory" for SQL extension nodes in the stylesheet tree. <br>
  */
public class SQL implements ExtensionElementFactory 
{
  /**
  * Identify the class to be used for stylesheet elements with a given local name.
  * The returned class must extend net.sf.saxon.style.StyleElement
  * @return null if the local name is not a recognised element type in this
  * namespace.
  */
  public Class getExtensionClass(String localname) {
    if (localname.equals("connect"))
      return SQLConnect.class;
    if (localname.equals("close"))
      return SQLClose.class;
    if (localname.equals("column"))
      return SQLColumn.class;
    if (localname.equals("delete"))
      return SQLDelete.class;
    if (localname.equals("insert"))
      return SQLInsert.class;
    if (localname.equals("property"))
      return SQLProperty.class;
    if (localname.equals("query"))
      return SQLQuery.class;
    if (localname.equals("update"))
      return SQLUpdate.class;
    return null;
  }
}

//
// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the
// License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations under the License.
//
// The Original Code is: all this file.
//
// The Initial Developer of the Original Code is Michael H. Kay.
//
// Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
// Contributor(s): none.
// 
// Acknowledgements:
// 
// A significant amount of new and/or modified code in this module
// was made possible by a grant from the Andrew W. Mellon Foundation,
// as part of the Melvyl Recommender Project.
//
