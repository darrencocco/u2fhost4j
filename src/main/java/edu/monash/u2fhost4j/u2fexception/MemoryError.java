/*
 * @copyright Monash University 2019
 * @author Darren Cocco (https://blog.segfault.id.au)
 * @licence LGPL v2.1
 */
package edu.monash.u2fhost4j.u2fexception;

public class MemoryError extends RuntimeException {
    public MemoryError(String message) {
        super(message);
    }
}
