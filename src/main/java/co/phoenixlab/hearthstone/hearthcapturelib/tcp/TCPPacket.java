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

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.time.Instant;

public class TCPPacket implements Comparable<TCPPacket> {

    public final Instant packetTime;

    public final TCPConnectionInfo connectionInfo;
    /**
     * @see org.jnetpcap.protocol.tcpip.Tcp#flags()
     */
    public final int tcpFlags;
    /**
     * The TCP SEQ number, used to track packet ordering and duplicate data.
     */
    public final long seqNumber;
    public final long ackNumber;
    public final byte[] payload;

    public TCPPacket(PcapPacket packet) {
        packetTime = Instant.now();
        Ip4 ip4 = packet.getHeader(new Ip4());
        Tcp tcp = packet.getHeader(new Tcp());
        connectionInfo = new TCPConnectionInfo(ip4.sourceToInt(), tcp.source(), ip4.destinationToInt(), tcp.destination());
        seqNumber = tcp.seq();
        ackNumber = tcp.ack();
        tcpFlags = tcp.flags();
        JBuffer storage = new JBuffer(JMemory.Type.POINTER);
        JBuffer packetPayload = tcp.peerPayloadTo(storage);
        payload = new byte[packetPayload.size()];
        packetPayload.getByteArray(0, payload);
    }

    public long nextExpectedSeqNumber() {
        //  If the ACK flag is set then SEQ must advance by at least one, otherwise SEQ is not incremented.
        return seqNumber + ((tcpFlags & 0x10) != 0 ? Math.max(1, payload.length) : 0);
    }

    @Override
    public int compareTo(TCPPacket o) {
        return Long.compare(seqNumber, o.seqNumber);
    }

    public int getByte(long byteNum) {
        long internByte = byteNum - seqNumber;
        boolean greater = internByte >= 0;
        boolean less = internByte < payload.length;
        if (greater && less) {
            return Byte.toUnsignedInt(payload[(int) internByte]);
        }
        if (!greater) {
            return -1;
        }
        return -2;
    }
}
