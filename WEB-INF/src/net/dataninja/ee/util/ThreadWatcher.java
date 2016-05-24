package net.dataninja.ee.util;


/*
dataninja copyright statement
 */
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Keeps an eye on threads, and logs those that appear to be "runaways".
 */
public class ThreadWatcher 
{
  private static HashMap beingWatched = new HashMap();
  private static Thread watcherThread = null;

  /**
   * Notifies the watcher that the current thread is about to begin an
   * operation that might turn into a runaway. If it does, the specified
   * description will be used in log messages. The thread should call
   * {@link #endWatch()} when it completes the operation.
   *
   * @param descrip     Description of the operation being started, used
   *                    for log messages.
   * @param normalTime  Max number of milliseconds the operation is expected
   *                    to take, after which warnings will be printed
   *                    about the thread being "runaway."
   * @param killTime    Number of milliseconds after which the thread should
   *                    be killed (by setting a flag that hopefully it will
   *                    check.)
   */
  public static void beginWatch(String descrip, long normalTime, long killTime) 
  {
    Thread curThread = Thread.currentThread();

    synchronized (beingWatched) 
    {
      // Make sure this thread isn't already being watched.
      if (beingWatched.containsKey(curThread)) {
        Trace.warning(
          "Thread began operation '" + descrip +
          "' but never called endWatch()");
      }

      // Add it to the list
      beingWatched.put(curThread,
                       new Entry(curThread, descrip, normalTime, killTime));

      // Make sure the watcher thread is running.
      if (watcherThread == null) 
      {
        watcherThread = new Thread() 
        {
          public void run() {
            watch();
          }
        };
        watcherThread.setDaemon(true);
        watcherThread.start();
      }
    } // synchronized
  } // beginWatch()

  /**
   * Notifies the watcher that the current thread has completed the
   * operation begun after {@link #beginWatch(String,long,long)}.
   */
  public static void endWatch() 
  {
    Thread curThread = Thread.currentThread();

    synchronized (beingWatched) 
    {
      // Make sure this thread is being watched.
      Entry e = (Entry)beingWatched.get(curThread);
      if (e == null) {
        Trace.warning(
          "Thread called endWatch() without first " + "calling beginWatch()");
        return;
      }

      // If it was runaway, report that it finally finished.
      if (e.runaway) {
        double secs = (System.currentTimeMillis() - e.startTime) / 1000.0;
        String secStr = DecimalFormat.getInstance().format(secs);
        Trace.warning(
          "This thread finally finished after " + secStr + " secs. Descrip: " +
          e.descrip);
      }

      // Remove it from the list
      beingWatched.remove(curThread);
    } // synchronized
  } // endWatch()

  /**
   * Counts the number of runaway threads at the moment.
   */
  public static int nRunaways() 
  {
    int count = 0;

    synchronized (beingWatched) 
    {
      Iterator iter = beingWatched.values().iterator();
      while (iter.hasNext()) {
        Entry e = (Entry)iter.next();
        if (e.runaway)
          ++count;
      }
    } // synchronized

    return count;
  } // endWatch()

  /**
   * Tells whether the specified thread has exceeded its kill limit and should
   * kill itself off.
   */
  public static boolean shouldDie(Thread thread) 
  {
    synchronized (beingWatched) {
      Entry e = (Entry)beingWatched.get(thread);
      if (e == null)
        return false;
      return e.kill;
    } // synchronized
  } // shouldDie()

  /**
   * This is the worker function that runs in a separate thread and keeps an
   * eye out for runaways.
   */
  private static void watch() 
  {
    try 
    {
      while (true) 
      {
        // Wait a while
        Thread.sleep(5100);

        // Check for and count runaways
        synchronized (beingWatched) 
        {
          long curTime = System.currentTimeMillis();
          int nRunaways = 0;

          Iterator iter = beingWatched.values().iterator();
          while (iter.hasNext()) 
          {
            Entry e = (Entry)iter.next();

            // If it has exceeded the kill time, request a kill. It
            // won't take effect unless and until the thread asks
            // if it should kill itself and does so.
            //
            if (!e.kill &&
                e.killTime > 0 &&
                (curTime - e.startTime) > e.killTime) 
            {
              e.kill = true;
              e.runaway = true;
              ++nRunaways;
              e.needPrint = true;
              continue;
            }

            // If it's not time to check for normal runaways, skip
            // until next time.
            //
            if (e.normalTime <= 0 || curTime < e.nextCheckTime) {
              if (e.runaway)
                ++nRunaways;
              continue;
            }

            // Mark this as a potential runaway, and mark it to be
            // printed below.
            e.runaway = true;
            ++nRunaways;
            e.needPrint = true;
          } // while iter

          // Now print out those that need it.
          int count = 0;
          curTime = System.currentTimeMillis();
          iter = beingWatched.values().iterator();
          while (iter.hasNext()) 
          {
            Entry e = (Entry)iter.next();
            if (e.runaway)
              ++count;

            if (!e.needPrint)
              continue;

            // Report it
            String id = Trace.getThreadId(e.thread);
            if (id == null)
              id = "";
            double secs = (curTime - e.startTime) / 1000.0;
            String secStr = DecimalFormat.getInstance().format(secs);
            Trace.warning(
              "Thread " + id + "may be " + "runaway (running " + secStr +
              " secs so far; runaway #" + count + " of " + nRunaways + "). " +
              (e.kill ? "Kill time exceeded. " : "") + "Descrip: " + e.descrip);

            // Report a stack trace if we have a new enough JVM
            try 
            {
              Class c = e.thread.getClass();
              Method m = c.getMethod("getStackTrace", new Class[0]);
              StackTraceElement[] stackTrace = (StackTraceElement[])m.invoke(
                e.thread,
                new Object[0]);

              StringBuffer buf = new StringBuffer();
              for (int i = 0; i < stackTrace.length; i++) {
                buf.append("    ");
                buf.append(stackTrace[i].toString());
                buf.append("\n");
              }

              Trace.warning("...stack snapshot: \n" + buf.toString());
            }
            catch (Exception exc) {
            }
          } // while iter

          // Finally, reschedule each thread we printed.
          curTime = System.currentTimeMillis();
          iter = beingWatched.values().iterator();
          while (iter.hasNext()) 
          {
            Entry e = (Entry)iter.next();
            if (!e.needPrint)
              continue;

            // Don't print twice.
            e.needPrint = false;

            // Reschedule this one.
            e.nextCheckTime = curTime + e.normalTime;
          } // while iter
        } // synchronized
      } // while true
    } // try
    catch (InterruptedException e) {
    }
  } // watch()

  /**
   * Keeps track of one thread we're watching.
   */
  private static class Entry 
  {
    Thread thread;
    String descrip;
    long startTime;
    long nextCheckTime;
    long normalTime;
    long killTime;
    boolean runaway = false;
    boolean needPrint = false;
    boolean kill = false;

    Entry(Thread t, String d, long n, long k) {
      thread = t;
      descrip = d;
      normalTime = n;
      killTime = k;
      startTime = System.currentTimeMillis();
      nextCheckTime = startTime + normalTime;
    }
  } // class Entry
} // class ThreadWatcher
