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

import org.junit.Test;

import static co.phoenixlab.hearthstone.hearthcapturelib.util.IP4Utils.*;
import static org.junit.Assert.*;

public class IP4UtilsTest {

    @Test
    public void testIpToByteArray() throws Exception {
        byte[] bytes = ipToByteArray(0xFFEEDDCC);
        byte[] expected = { (byte) 0xFF, (byte) 0xEE, (byte) 0xDD, (byte) 0xCC };
        assertArrayEquals(expected, bytes);
    }

    @Test
    public void testIpToByteArrayStr() throws Exception {
        byte[] bytes = ipToByteArray("10.128.8.255");
        byte[] expected = { (byte) 10, (byte) 128, (byte) 8, (byte) 0xFF };
        assertArrayEquals(expected, bytes);
    }

    @Test
    public void testIntToIp() throws Exception {
        String ip = intToIp(0xFF801005);
        assertEquals("255.128.16.5", ip);
    }

    @Test
    public void testIpToInt() throws Exception {
        int i = ipToInt(new byte[] {(byte) 0xFF, (byte) 0x80, (byte) 0x02, (byte) 0x10});
        assertEquals(0xFF800210, i);
    }

    @Test
    public void testIpToIntStr() throws Exception {
        int i = ipToInt("255.128.16.5");
        assertEquals(0xFF801005, i);

    }
}
