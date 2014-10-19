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

package co.phoenixlab.hearthstone.hearthcapturelib.tcp;

import co.phoenixlab.hearthstone.hearthcapturelib.util.IP4Utils;

/**
 * Represents an IP address and port pair.
 */
public class TCPAddressPortPair {

    public static final TCPAddressPortPair NO_REMOTE_ADDRESS = new TCPAddressPortPair(0, 0);

    public final int ip;
    public final int port;

    public TCPAddressPortPair(int ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TCPAddressPortPair that = (TCPAddressPortPair) o;
        return ip == that.ip && port == that.port;
    }

    @Override
    public int hashCode() {
        int result = ip;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "TCPAddress{" +
                "ip=" + IP4Utils.intToIp(ip) +
                ", port=" + port +
                '}';
    }
}
