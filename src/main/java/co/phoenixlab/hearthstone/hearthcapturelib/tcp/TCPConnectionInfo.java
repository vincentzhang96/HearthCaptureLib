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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Contains information about a TCP connection, namely the TCPAddressPortPairs of both the source and destinations.
 */
public class TCPConnectionInfo {

    /**
     * The IP address that this packet originated from.
     */
    public final int sourceIPAddr;
    /**
     * The port that this packet originated from.
     */
    public final int sourcePort;
    /**
     * The IP address that this packet is being sent to.
     */
    public final int destinationIPAddr;
    /**
     * The port that this packet is being sent to.
     */
    public final int destinationPort;

    public TCPConnectionInfo(int sourceIPAddr, int sourcePort, int destinationIPAddr, int destinationPort) {
        this.sourceIPAddr = sourceIPAddr;
        this.sourcePort = sourcePort;
        this.destinationIPAddr = destinationIPAddr;
        this.destinationPort = destinationPort;

    }

    /**
     * Get the TCPAddressPortPair representing the remote end of the connection, or NO_REMOTE_ADDRESS (0.0.0.0:0) if there is no remote end (ie both are local).
     */
    public TCPAddressPortPair getRemoteAddress() {
        if (isSourceRemote()) {
            return new TCPAddressPortPair(sourceIPAddr, sourcePort);
        } else if (isDestinationRemote()) {
            return new TCPAddressPortPair(destinationIPAddr, destinationPort);
        } else {
            return TCPAddressPortPair.NO_REMOTE_ADDRESS;
        }
    }

    /**
     * Returns whether or not the source is the remote address.
     */
    public boolean isSourceRemote() {
        return isRemote(sourceIPAddr);
    }

    /**
     * Returns whether or not the destination is the remote address.
     */
    public boolean isDestinationRemote() {
        return isRemote(destinationIPAddr);
    }

    /**
     * Checks if a given IP address represents a remote location.
     *
     * @param ip An IPv4 address in a 32-bit integer, network endian.
     * @return true if the address is remote, false if it is a local, loopback, or site local address.
     */
    public static boolean isRemote(int ip) {
        try {
            InetAddress dstAddr = Inet4Address.getByAddress(IP4Utils.ipToByteArray(ip));
            return !(dstAddr.isSiteLocalAddress() || dstAddr.isAnyLocalAddress() || dstAddr.isLoopbackAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException("ipToByteArray returned an invalid array!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TCPConnectionInfo that = (TCPConnectionInfo) o;
        return destinationIPAddr == that.destinationIPAddr && destinationPort == that.destinationPort && sourceIPAddr == that.sourceIPAddr && sourcePort == that.sourcePort;
    }

    @Override
    public int hashCode() {
        int result = sourceIPAddr;
        result = 31 * result + sourcePort;
        result = 31 * result + destinationIPAddr;
        result = 31 * result + destinationPort;
        return result;
    }

    @Override
    public String toString() {
        return "TCPConnectionInfo{" +
                "sourceIPAddr=" + IP4Utils.intToIp(sourceIPAddr) +
                ", sourcePort=" + sourcePort +
                ", destinationIPAddr=" + IP4Utils.intToIp(destinationIPAddr) +
                ", destinationPort=" + destinationPort +
                '}';
    }
}
