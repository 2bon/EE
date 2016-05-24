package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */
import net.dataninja.ee.textEngine.QueryRequest;

/**
 * Stores a facet specification, as part of a {@link QueryRequest}.
 *
 * @author Rick Li
 */
public class FacetSpec 
{
  /** Name of the meta-data field to group by */
  public String field;

  /** Selection of groups */
  public GroupSelector groupSelector;

  /** How to sort the groups. Currently "value" and "totalDocs" are the only
   *  permissible values.
   */
  public String sortGroupsBy = "totalDocs";

  /** Whether to include empty groups by default */
  public boolean includeEmptyGroups = false;

  /** If any documents are selected, this field specifies which meta-data
   *  field(s) to sort the documents by.
   */
  public String sortDocsBy = "score";
} // class FacetSpec
