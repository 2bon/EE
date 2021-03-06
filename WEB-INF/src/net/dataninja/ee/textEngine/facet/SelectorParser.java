/* Generated By:JavaCC: Do not edit this line. SelectorParser.java */
package net.dataninja.ee.textEngine.facet;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class SelectorParser implements SelectorParserConstants {

//////////////////////////////////////////////////////////////////////////////
// Top-level
//////////////////////////////////////////////////////////////////////////////
  final public GroupSelector parse() throws ParseException {
  GroupSelector s;
    s = union();
    jj_consume_token(0);
                      {if (true) return s;}
    throw new Error("Missing return statement in function");
  }

//////////////////////////////////////////////////////////////////////////////
// A number of expressions, separated by "|"
//////////////////////////////////////////////////////////////////////////////
  final public GroupSelector union() throws ParseException {
  ArrayList list = new ArrayList();
  GroupSelector s;
    s = expr();
               list.add(s);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BAR:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      jj_consume_token(BAR);
      s = expr();
                       list.add(s);
    }
    if(list.size() == 1)
        {if (true) return (GroupSelector) list.get(0);}

    GroupSelector[] array = (GroupSelector[])
        list.toArray(new GroupSelector[list.size()]);
    {if (true) return new UnionSelector(array);}
    throw new Error("Missing return statement in function");
  }

//////////////////////////////////////////////////////////////////////////////
// One or more levels, for instance "A::B::C#all"
//////////////////////////////////////////////////////////////////////////////
  final public GroupSelector expr() throws ParseException {
  GroupSelector root = new RootSelector();
  GroupSelector prev = root;
  GroupSelector s;
  boolean gotDocs = false;
    s = level(root);
                    prev = s;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DOUBLE_COLON:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
      jj_consume_token(DOUBLE_COLON);
                      s = new ChildSelector(); prev.setNext(s); prev = s;
      s = level(prev);
                      prev = s;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case POUND:
      jj_consume_token(POUND);
      s = docs();
                         prev.setNext(s); prev = s; gotDocs = true;
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    if(!gotDocs) {
        s = new MarkSelector();
        prev.setNext(s);
        prev = s;
    }
    {if (true) return root;}
    throw new Error("Missing return statement in function");
  }

//////////////////////////////////////////////////////////////////////////////
// A single level, consists of a name or * followed by optional filters
//////////////////////////////////////////////////////////////////////////////
  final public GroupSelector level(GroupSelector prev) throws ParseException {
  GroupSelector s;
  GroupSelector prevPrev = null;
    s = name();
                      if(s != null) {
                          prev.setNext(s);
                          prevPrev = prev;
                          prev = s;
                      }
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPEN_BRACKET:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_3;
      }
      jj_consume_token(OPEN_BRACKET);
      s = filter();
      jj_consume_token(CLOSE_BRACKET);
                      prev.setNext(s); prevPrev = prev; prev = s;
    }
    {if (true) return prev;}
    throw new Error("Missing return statement in function");
  }

//////////////////////////////////////////////////////////////////////////////
// One name
//////////////////////////////////////////////////////////////////////////////
  final public GroupSelector name() throws ParseException {
  StringBuffer buf = new StringBuffer();
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRING:
      t = jj_consume_token(STRING);
                      String s = t.toString().replace("\\\"", "\"");
                      {if (true) return new NameSelector(s.substring(1, s.length()-1));}
      break;
    case COLON:
    case STAR:
    case DASH:
    case EQUAL:
    case NUMBER:
    case TERM:
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case TERM:
          t = jj_consume_token(TERM);
                      buf.append(t.toString());
          break;
        case NUMBER:
          t = jj_consume_token(NUMBER);
                      buf.append(t.toString());
          break;
        case DASH:
          t = jj_consume_token(DASH);
                      buf.append(t.toString());
          break;
        case STAR:
          t = jj_consume_token(STAR);
                      buf.append(t.toString());
          break;
        case COLON:
          t = jj_consume_token(COLON);
                      buf.append(t.toString());
          break;
        case EQUAL:
          t = jj_consume_token(EQUAL);
                      buf.append(t.toString());
          break;
        default:
          jj_la1[4] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COLON:
        case STAR:
        case DASH:
        case EQUAL:
        case NUMBER:
        case TERM:
          ;
          break;
        default:
          jj_la1[5] = jj_gen;
          break label_4;
        }
      }
    String str = buf.toString();
    if(str.equals("*"))
        {if (true) return null;}
    if(str.equals("**"))
        {if (true) return new DescendantSelector();}
    {if (true) return new NameSelector(buf.toString());}
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

//////////////////////////////////////////////////////////////////////////////
// Various kinds of filters are allowed
//////////////////////////////////////////////////////////////////////////////
  final public GroupSelector filter() throws ParseException {
  GroupSelector s;
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NUMBER:
      s = rangeFilter();
                          {if (true) return s;}
      break;
    case TERM:
      t = jj_consume_token(TERM);
          if(t.toString().equalsIgnoreCase("topChoices"))
              s = new TopChoiceSelector();
          else if(t.toString().equalsIgnoreCase("nonEmpty"))
              s = new EmptySelector(false);
          else if(t.toString().equalsIgnoreCase("empty"))
              s = new EmptySelector(true);
          else if(t.toString().equalsIgnoreCase("unselected"))
              s = new SelectedSelector(false);
          else if(t.toString().equalsIgnoreCase("selected"))
              s = new SelectedSelector(true);
          else if(t.toString().equalsIgnoreCase("siblings"))
              s = new SiblingSelector();
          else if(t.toString().equalsIgnoreCase("page"))
              s = new PageSelector();
          else if(t.toString().equalsIgnoreCase("singleton"))
              s = new SingletonSelector();
          else
              {if (true) throw new ParseException("Unknown filter '" + t.toString() + "'");}
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPEN_PAREN:
        jj_consume_token(OPEN_PAREN);
        t = jj_consume_token(TERM);
                    if(!t.toString().equalsIgnoreCase("size"))
                        {if (true) throw new ParseException("Unknown parameter '" + t.toString() + "'");}
        jj_consume_token(EQUAL);
        t = jj_consume_token(NUMBER);
            if(s instanceof PageSelector) {
                int size = Integer.parseInt(t.toString());
                ((PageSelector)s).setPageSize(size);
            }
            else
                {if (true) throw new ParseException("Argument '" + t.toString() + "' not allowed here");}
        jj_consume_token(CLOSE_PAREN);
        break;
      default:
        jj_la1[7] = jj_gen;
        ;
      }
        {if (true) return s;}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

//////////////////////////////////////////////////////////////////////////////
// Range filter
//////////////////////////////////////////////////////////////////////////////
  final public GroupSelector rangeFilter() throws ParseException {
  Token t;
  int from;
  int to;
    t = jj_consume_token(NUMBER);
                  from = to = Integer.parseInt(t.toString());
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DASH:
      jj_consume_token(DASH);
      t = jj_consume_token(NUMBER);
                    to = Integer.parseInt(t.toString());
      break;
    default:
      jj_la1[9] = jj_gen;
      ;
    }
    {if (true) return new RangeSelector(from - 1, to - from + 1);}
    throw new Error("Missing return statement in function");
  }

//////////////////////////////////////////////////////////////////////////////
// Docs specification
//////////////////////////////////////////////////////////////////////////////
  final public GroupSelector docs() throws ParseException {
  Token t;
  int from;
  int to = 999999999;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TERM:
      t = jj_consume_token(TERM);
                      if(!t.toString().equals("all")) {
                          {if (true) throw new ParseException(
                              "Found '" + t.toString() +
                              "' but expected 'all' or '<NUMBER> - <NUMBER>");}
                      }
                      {if (true) return new DocsSelector(0, 999999999);}
      break;
    case NUMBER:
      t = jj_consume_token(NUMBER);
                      from = Integer.parseInt(t.toString());
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DASH:
        jj_consume_token(DASH);
        t = jj_consume_token(NUMBER);
                      to = Integer.parseInt(t.toString());
        break;
      default:
        jj_la1[10] = jj_gen;
        ;
      }
    {if (true) return new DocsSelector(from - 1, to - from + 1);}
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  public SelectorParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[12];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x80,0x20,0x10,0x200,0x1e140,0x1e140,0x3e140,0x800,0x18000,0x2000,0x2000,0x18000,};
   }

  public SelectorParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public SelectorParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SelectorParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  public SelectorParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SelectorParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  public SelectorParser(SelectorParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  public void ReInit(SelectorParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[18];
    for (int i = 0; i < 18; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 12; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 18; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

}
