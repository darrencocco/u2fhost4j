/*
 * @copyright Monash University 2019
 * @author Darren Cocco (https://blog.segfault.id.au)
 * @licence LGPL v2.1
 */
package edu.monash.u2fhost4j.u2fexception;

import edu.monash.u2fhost4j.U2FException;

public class TimeoutError extends U2FException {
    public TimeoutError(String message) {
        super(message);
    }
}