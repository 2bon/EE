package net.dataninja.ee.textEngine;


/**
net.dataninja copyright statement
 */

/**
 * Thrown if a problem (most likely an I/O error) occurs while loading a
 * hit. This is fairly unlikely, so this class is a RuntimeException, meaning
 * that it isn't required to be declared by every method that might have to
 * deal with one.
 */
class HitLoadException extends RuntimeException 
{
  HitLoadException(Exception base) {
    super(base);
  }
}
