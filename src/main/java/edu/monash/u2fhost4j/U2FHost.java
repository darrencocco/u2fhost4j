/*
 * @copyright Monash University 2019
 * @author Darren Cocco (https://blog.segfault.id.au)
 * @licence LGPL v2.1
 */
package edu.monash.u2fhost4j;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.ochafik.lang.jnaerator.runtime.NativeSizeByReference;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.json.JSONObject;

import static edu.monash.u2fhost4j.Libu2fHostLibrary.u2fh_cmdflags.U2FH_REQUEST_USER_PRESENCE;

public class U2FHost {
    private static U2FHost ourInstance = null;

    private static int responseLength = 2048;

    private Libu2fHostLibrary u2fLib = Libu2fHostLibrary.INSTANCE;
    private Pointer devs;
    private int maxDevs;
    private static int initFlags = 0;

    public static U2FHost getInstance() throws U2FException {
        if (ourInstance == null) {
            ourInstance = new U2FHost();
        }
        return ourInstance;
    }

    private U2FHost() throws U2FException {
        this.connect();
    }

    private int connect() throws U2FException {
        PointerByReference devStar = new PointerByReference();
        IntByReference max_devs = new IntByReference();
        int responseCode = 1;
        responseCode = u2fLib.u2fh_global_init(initFlags);
        checkAndThrow(responseCode);
        responseCode = u2fLib.u2fh_devs_init(devStar);
        checkAndThrow(responseCode);
        devs = devStar.getValue();
        responseCode = u2fLib.u2fh_devs_discover(devs, max_devs);
        checkAndThrow(responseCode);
        maxDevs = max_devs.getPointer().getInt(0);
        return responseCode;
    }

    public void setConnectFlags(int connectFlags) {
        initFlags = connectFlags;
    }

    private void disconnect() {
        u2fLib.u2fh_devs_done(devs);
        u2fLib.u2fh_global_done();
    }

    public int reconnect() throws U2FException {
        this.disconnect();
        return this.connect();
    }

    private void checkAndThrow(int returnCode) throws U2FException {
        if (returnCode != 0) {
            U2FException.throwException(returnCode);
        }
    }

    public int getDeviceCount() {
        return maxDevs + 1;
    }

    public String getDescription(int index) throws U2FException {
        int responseCode = 1;
        Memory response = new Memory(responseLength);
        NativeSize ns2 = new NativeSize(responseLength);
        NativeSizeByReference response_len = new NativeSizeByReference(ns2);
        responseCode = u2fLib.u2fh_get_device_description(devs, index, response, response_len);
        checkAndThrow(responseCode);
        return Native.toString(response.getByteArray(0, response_len.getPointer().getInt(0)));
    }

    public String authenticate(String origin, JSONObject challengeObject) throws U2FException {
        int responseCode = 1;
        String challenge = challengeObject.toString();
        Memory response = new Memory(responseLength);
        NativeSize ns2 = new NativeSize(responseLength);
        NativeSizeByReference response_len = new NativeSizeByReference(ns2);
        responseCode = u2fLib.u2fh_authenticate2(devs, challenge, origin, response, response_len, U2FH_REQUEST_USER_PRESENCE);
        checkAndThrow(responseCode);
        return Native.toString(response.getByteArray(0, response_len.getPointer().getInt(0)));
    }

    public String authenticate(String origin, String challenge, String version, String keyHandle, String appId) throws U2FException {
        JSONObject challengeObject = new JSONObject();
        challengeObject.put("challenge", challenge);
        challengeObject.put("version", version);
        challengeObject.put("keyHandle", keyHandle);
        challengeObject.put("appId", appId);
        return authenticate(origin, challengeObject);
    }
}