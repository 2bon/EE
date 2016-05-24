package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Handles tedious details of making a little regression test for a given
 * class.
 *
 * @author Rick Li
 */
public abstract class Tester 
{
  /**
   * List of all tests (or at least, tests for all classes that have been
   * loaded.
   */
  private static LinkedList allTests = new LinkedList();

  /** Name of this test */
  private String name;

  /** True after test has been tried */
  private boolean testedAlready;

  /** Add this test to the global list of tests */
  public Tester(String name) {
    this.name = name;
    allTests.add(this);
  }

  /**
   * Run all registered tests. It doesn't matter which runs first, since if
   * a test has a dependency it should directly call those tests it depends
   * on.
   */
  public static final void testAll() {
    for (Iterator iter = allTests.iterator(); iter.hasNext();)
      ((Tester)iter.next()).test();
  } // testAll()

  /**
   * Run this particular test. If it has already been run, the test is
   * skipped.
   */
  public final void test() 
  {
    // Don't run this test again if it already ran.
    if (testedAlready)
      return;
    testedAlready = true;

    // Make sure assertions are turned on.
    boolean ok = false;
    assert (ok = true) == true;
    if (!ok)
      throw new AssertionError("Must turn on assertions for test()");

    // Run the test
    Trace.info("Running test '" + name + "'...");
    try {
      testImpl();
    }
    catch (Exception e) {
      Trace.error("... Test '" + name + "' failed: " + e);
      if (e instanceof RuntimeException)
        throw (RuntimeException)e;
      else
        throw new RuntimeException(e);
    }
    Trace.info("... Test '" + name + "' passed.");
  } // test()

  /**
   * Derived classes should override this method to perform the actual
   * work of the test.
   */
  protected abstract void testImpl()
    throws Exception;
} // class Tester
