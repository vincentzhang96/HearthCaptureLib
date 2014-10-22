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

import co.phoenixlab.hearthstone.hearthcapturelib.packets.CapturePacket;
import co.phoenixlab.hearthstone.hearthcapturelib.tcp.TCPPacket;
import co.phoenixlab.hearthstone.hearthcapturelib.util.MThread;

/**
 * A packet queue takes in raw TCP packets, processes them, and then provides a method of retrieving the parsed packets in a queue-like fashion.
 *
 * @author Vincent Zhang
 */
public interface PacketQueue {

    /**
     * Retrieves the next CapturePacket, blocking until it's available.
     *
     * @return The next CapturePacket, or null if the queue has been closed.
     * @throws java.lang.InterruptedException If the thread was interrupted while waiting for the next packet.
     */
    @MThread("user")
    CapturePacket next() throws InterruptedException;

    /**
     * Retrieves, but does not remove, the next CapturePacket, or returns null if the next CapturePacket is
     * not available yet.
     *
     * @return The next CapturePacket, or null if there is no available CapturePacket.
     */
    @MThread("user")
    CapturePacket peek();

    /**
     * Checks if there's a CapturePacket available.
     *
     * @return true if the next CapturePacket is immediately available, false otherwise.
     */
    @MThread("user")
    boolean hasNext();

    /**
     * Checks if the queue has been closed by the producer to indicate end of stream.
     *
     * @return true if the queue is closed, false if open.
     */
    @MThread("user")
    boolean isClosed();

    /**
     * Adds a raw packet to be processed.
     *
     * @param packet The raw packet to add.
     */
    @MThread("listener")
    void put(TCPPacket packet);

    /**
     * Closes the queue, indicating end of stream.
     */
    @MThread("listener")
    void close();

    /**
     * Get the time at which this packet queue was opened.
     */
    long getCaptureStartTime();

}
