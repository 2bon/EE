package net.dataninja.ee.cache;


/**
net.dataninja copyright statement
 */

/**
 * Base class for all dependencies. The validate() method must be supplied
 * by any derived class, and is used to check if the dependency is still
 * valid.
 */
public abstract class Dependency 
{
  /**
   * Check if dependency is still valid. Must be supplied by derived class.
   *
   * @return  true if valid, false if stale.
   */
  public abstract boolean validate();
} // class Dependency
