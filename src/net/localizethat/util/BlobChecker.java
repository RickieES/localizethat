/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.localizethat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class with static methods to calculate MD5 hashes from byte arrays
 *
 * @author rpalomares
 */
public class BlobChecker {

    /**
     * Calculates and returns the MD5 hash from a possibly long string
     * @param longString the string whose MD5 we want to calculate
     * @return the resulting MD5
     */
    public static String getMD5Hash(String longString) {
        return getMD5Hash(longString.getBytes());
    }

    /**
     * Calculates and returns the MD5 hash from a byte sequence
     * @param byteSequence the byte sequence whose MD5 we want to calculate
     * @return the resulting MD5
     */
    public static String getMD5Hash(byte[] byteSequence) {
        StringBuilder md5 = new StringBuilder(32);
        MessageDigest digest;
        byte[] hash;

        try {
            digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(byteSequence);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException ex) {
            hash = new byte[0];
        }

        for (int i = 0; i < hash.length; i++) {
            String s = Integer.toHexString(0xFF & hash[i]).toUpperCase();
            md5.append(s.length() < 2 ? "0" : "").append(s);
        }
        return md5.toString();
    }

    /**
     * Private default constructor to avoid instantiating this class
     */
    private BlobChecker() {
    }
}
