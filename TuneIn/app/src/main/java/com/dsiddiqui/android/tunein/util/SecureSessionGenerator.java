package com.dsiddiqui.android.tunein.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Adapted from StackOverflow community Wiki.
 * Created by dsiddiqui on 15-11-07.
 */
public class SecureSessionGenerator {

    // Instance Variables

    private static SecureSessionGenerator sInstance = new SecureSessionGenerator();

    private SecureRandom random = new SecureRandom();

    // Constructors

    public static SecureSessionGenerator getInstance() {
        return sInstance;
    }

    private SecureSessionGenerator() {}

    // Unique session generator

    public int nextSessionId() {
        return new BigInteger(130, random).intValue();
    }

}
