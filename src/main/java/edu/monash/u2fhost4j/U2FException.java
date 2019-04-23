/*
 * @copyright Monash University 2019
 * @author Darren Cocco (https://blog.segfault.id.au)
 * @licence LGPL v2.1
 */
package edu.monash.u2fhost4j;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import edu.monash.u2fhost4j.u2fexception.*;

import static edu.monash.u2fhost4j.Libu2fHostLibrary.u2fh_rc.*;

public class U2FException extends Exception {
    public U2FException(String message) {
        super(message);
    }
    private static Libu2fHostLibrary u2fLib = Libu2fHostLibrary.INSTANCE;
    public static void throwException(int u2fErrorCode) throws U2FException {
        switch (u2fErrorCode) {
            case U2FH_MEMORY_ERROR:
                throw new MemoryError(getErrorMessage(u2fErrorCode));
            case U2FH_TRANSPORT_ERROR:
                throw new TransportError(getErrorMessage(u2fErrorCode));
            case U2FH_JSON_ERROR:
                throw new JsonError(getErrorMessage(u2fErrorCode));
            case U2FH_BASE64_ERROR:
                throw new Base64Error(getErrorMessage(u2fErrorCode));
            case U2FH_NO_U2F_DEVICE:
                throw new NoDeviceError(getErrorMessage(u2fErrorCode));
            case U2FH_AUTHENTICATOR_ERROR:
                throw new AuthenticatorError(getErrorMessage(u2fErrorCode));
            case U2FH_TIMEOUT_ERROR:
                throw new TimeoutError(getErrorMessage(u2fErrorCode));
            case U2FH_SIZE_ERROR:
                throw new SizeError(getErrorMessage(u2fErrorCode));
            default:
                throw new RuntimeException("Unknown U2F Host error code (" + u2fErrorCode + ")");
        }
    }

    private static String getErrorMessage(int u2fErrorCode) {
        Pointer namePtr;
        // Warning this is a memory leak.
        namePtr = u2fLib.u2fh_strerror_name(u2fErrorCode);
        String name = Native.toString(namePtr.getByteArray(0, 2048));

        Pointer descriptionPtr;
        // Warning this is a memory leak.
        descriptionPtr = u2fLib.u2fh_strerror(u2fErrorCode);
        String description = Native.toString(descriptionPtr.getByteArray(0, 2048));

        return name + ": " + description;
    }
}