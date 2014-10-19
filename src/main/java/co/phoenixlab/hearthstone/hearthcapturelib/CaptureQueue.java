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

package co.phoenixlab.hearthstone.hearthcapturelib;

/**
 * A pair of inbound and outbound packet queues. This is the object returned by {@link HearthstoneCapturer#listen()} that clients use to receive packets from
 * the capture system.
 *
 * @author Vincent Zhang
 */
public final class CaptureQueue {

    private final PacketQueue outboundPackets;
    private final PacketQueue inboundPackets;

    public CaptureQueue(PacketQueue outboundPackets, PacketQueue inboundPackets) {
        this.outboundPackets = outboundPackets;
        this.inboundPackets = inboundPackets;
    }

    /**
     * Closes both packet queues.
     */
    public void close() {
        getOutboundPackets().close();
        getInboundPackets().close();
    }

    /**
     * Checks whether or not the packet queues are closed.
     *
     * @return True if either or both queues are closed, or false if neither are closed.
     */
    public boolean isClosed() {
        return getOutboundPackets().isClosed() || getInboundPackets().isClosed();
    }

    /**
     * Get the packet queue handling outbound (client to server) packets.
     */
    public PacketQueue getOutboundPackets() {
        return outboundPackets;
    }

    /**
     * Get the packet queue handling inbound (server to client) packets.
     */
    public PacketQueue getInboundPackets() {
        return inboundPackets;
    }
}
