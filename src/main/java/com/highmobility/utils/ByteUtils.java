//
// HMKit Utils
//
// Copyright © 2017 High Mobility. All rights reserved.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see http://www.gnu.org/licenses/.
//
// Please inquire about commercial licensing options at
// licensing@high-mobility.com
//

package com.highmobility.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ByteUtils {
    final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Transform a byte array into a hex string
     *
     * @param bytes The byte array
     * @return The bytes in hex format.
     */
    public static String hexFromBytes(byte[] bytes) {
        if (bytes == null) return "(null)";
        if (bytes.length == 0) return "[]";

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    /**
     * Transform a hex string into a byte array
     *
     * @param s The hex string
     * @return The hex string translated to bytes
     */
    public static byte[] bytesFromHex(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    /**
     * Transform a hex string into a byte array. Also checks if the input is in hex characters.
     *
     * @param s The hex string
     * @return The hex string translated to bytes
     */
    public static byte[] bytesFromHexCheckInput(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        boolean[] hexArrayContains = new boolean[2];
        for (int i = 0; i < len; i += 2) {
            Character hexFirstCharacter = Character.toUpperCase(s.charAt(i));
            Character hexSecondCharacter = Character.toUpperCase(s.charAt(i + 1));
            for (int j = 0; j < hexArray.length; j++) {

                if (hexArrayContains[0] == false && hexFirstCharacter == hexArray[j]) {
                    hexArrayContains[0] = true;
                }

                if (hexArrayContains[1] == false && hexSecondCharacter == hexArray[j]) {
                    hexArrayContains[1] = true;
                }
            }

            if (hexArrayContains[0] == false || hexArrayContains[1] == false) {
                throw new IllegalArgumentException("Not a hex string.");
            } else {
                hexArrayContains[0] = false;
                hexArrayContains[1] = false;
            }

            data[i / 2] = (byte) ((Character.digit(hexFirstCharacter, 16) << 4) + Character
                    .digit(hexSecondCharacter, 16));
        }

        return data;
    }

    /**
     * Tries to parse input to a byte array. First tries to parse the input as hex, then as base64.
     * Base64 string is detected quite fast but in case of hex it verifies all of the string's
     * characters. {@link #bytesFromHex(String)} is faster for strings that are known to be hex.
     *
     * @param input The input, in hex or base64.
     * @return The byte[] if parsing was successful/
     */
    public static byte[] bytesFromHexOrBase64(String input) {
        byte[] result;
        try {
            result = ByteUtils.bytesFromHexCheckInput(input);
            return result;
        } catch (Exception e) {
            try {
                result = Base64.decode(input);
                return result;
            } catch (Exception e2) {
                throw new IllegalArgumentException("Cannot parse the input string to a byte array" +
                        ": " + e2.getMessage());
            }
        }
    }

    /**
     * Concatenates two byte arrays
     *
     * @param a A byte array that will get concatenated
     * @param b A byte array that will get concatenated
     * @return The concatenated byte array
     */
    public static byte[] concatBytes(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    /**
     * Concatenates a byte array and a byte
     *
     * @param a A byte array that will get concatenated
     * @param b A byte array that will get concatenated
     * @return The concatenated byte array
     */
    public static byte[] concatBytes(byte[] a, byte b) {
        int aLen = a.length;

        byte[] c = new byte[aLen + 1];
        System.arraycopy(a, 0, c, 0, aLen);
        c[c.length - 1] = b;

        return c;
    }

    /**
     * Set the bytes in an array
     *
     * @param inArray The array the bytes are set in
     * @param toBytes The bytes that are set
     * @param offset  The offset of the set bytes
     */
    public static void setBytes(byte[] inArray, byte[] toBytes, int offset) {
        for (int i = offset; i < offset + toBytes.length; i++) {
            if (i > inArray.length - 1) return;
            inArray[offset + (i - offset)] = toBytes[i - offset];
        }
    }

    /**
     * Test whether a bit is set in a byte.
     *
     * @param fromByte The byte.
     * @param bitIndex The bit location.
     * @return True if the bit is 1.
     */
    public static boolean getBit(byte fromByte, int bitIndex) {
        return ((fromByte >> bitIndex) & 1) == 1;
    }

    /**
     * Does this byte array begin with match array content?
     *
     * @param source Byte array to examine
     * @param match  Byte array to locate in <code>source</code>
     * @return true If the starting bytes are equal
     */
    public static boolean startsWith(byte[] source, byte[] match) {
        return startsWith(source, 0, match);
    }

    /**
     * Does this byte array begin with match array content?
     *
     * @param source Byte array to examine
     * @param offset An offset into the <code>source</code> array
     * @param match  Byte array to locate in <code>source</code>
     * @return true If the starting bytes are equal
     */
    public static boolean startsWith(byte[] source, int offset, byte[] match) {

        if (match.length > (source.length - offset)) {
            return false;
        }

        for (int i = 0; i < match.length; i++) {
            if (source[offset + i] != match[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert a byte array into UUID
     *
     * @param bytes The byte array
     * @return The UUID
     */
    public static UUID UUIDFromByteArray(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        return uuid;
    }

    /**
     * Convert a mac string into byte array
     *
     * @param mac The mac address in String format eg FF:FF:FF:FF:FF:FF
     * @return The
     */
    public static byte[] bytesFromMacString(String mac) {
        String[] macAddressParts = mac.split(":");

        // convert hex string to byte values
        byte[] macAddressBytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            Integer hex = Integer.parseInt(macAddressParts[i], 16);
            macAddressBytes[i] = hex.byteValue();
        }

        return macAddressBytes;
    }

    /**
     * Trim the bytes to given length eg remove the elements that are over length.
     *
     * @param bytes  The bytes that will get trimmed.
     * @param length The length of the trimmed byte array
     * @return The trimmed byte array
     */
    public static byte[] trimmedBytes(byte[] bytes, int length) {
        if (bytes.length == length) return bytes;

        byte[] trimmedBytes = new byte[length];

        for (int i = 0; i < length; i++) {
            trimmedBytes[i] = bytes[i];
        }

        return trimmedBytes;
    }

    /**
     * Reverse the byte array
     *
     * @param array The array to be reversed
     */
    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }
}
