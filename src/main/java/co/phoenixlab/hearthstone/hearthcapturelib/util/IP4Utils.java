/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Vincent Zhang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.phoenixlab.hearthstone.hearthcapturelib.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.StringTokenizer;

/**
 * A few utilities to convert between String, integer, and byte array representations of IP addresses.
 *
 * @author Vincent Zhang
 */
public class IP4Utils {

    public static byte[] ipToByteArray(int ip) {
        byte[] ret = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(ip);
        buffer.flip();
        buffer.get(ret);
        return ret;
    }

    public static byte[] ipToByteArray(String ip) {
        return ipToByteArray(ipToInt(ip));
    }

    public static String intToIp(int ip) {
        return String.format("%s.%s.%s.%s", (ip >> 24) & 0xFF, (ip >> 16) & 0xFF, (ip >> 8) & 0xFF, ip & 0xFF);
    }

    public static int ipToInt(byte[] ip) {
        if (ip.length != 4) {
            throw new IllegalArgumentException("Array must be 4 bytes");
        }
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).put(ip).getInt(0);
    }

    public static int ipToInt(String ip) {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");
        if (tokenizer.countTokens() != 4) {
            throw new IllegalArgumentException("Invalid format");
        }
        int ret = 0;
        for (int i = 3; i >= 0; i--) {
            String token = tokenizer.nextToken();
            int val = Integer.parseInt(token);
            ret |= (val << (8 * i)) & (0xFF << (8 * i));
        }
        return ret;
    }


}
