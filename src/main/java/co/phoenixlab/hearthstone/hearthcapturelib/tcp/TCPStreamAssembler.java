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

import co.phoenixlab.hearthstone.hearthcapturelib.util.MThread;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A TCP/IP packet assembler, readable as an InputStream.
 * <p>
 * This class accepts TCP/IP packets, collects them, and orders them to create a continuous stream of bytes that can be read from as an InputStream.
 *
 * @author Vincent Zhang
 */
public class TCPStreamAssembler
        extends InputStream {

    /*
    TODO Fix packet ordering corner cases.
    Packet ordering can be a little bit wonky when we get packets out of order in a certain way that causes the ordering system to dispose of a packet
    even though it hasn't been read yet, causing the consuming side to wait forever for a packet that has already been skipped.
     */

    private PriorityBlockingQueue<TCPPacket> pendingPackets;
    private final Object notifyObject;
    private final AtomicBoolean notifyAck;
    private long startingSeqNumber;
    private long currentSeqNumber;
    private long nextExpectedSeqNumber;
    private long currentByteIndex;
    /**
     * Number of bytes read from the stream. Also indicates the index of the next byte to be read.
     */
    private long bytesRead; //  if we've read byte 0, bytesRead = 1, index of the next byte is 1.
    private TCPPacket workingPacket;

    public TCPStreamAssembler() {
        pendingPackets = new PriorityBlockingQueue<>();
        currentSeqNumber = -1L;
        startingSeqNumber = -1L;
        bytesRead = 0L;
        currentByteIndex = -1L;
        notifyObject = new Object();
        notifyAck = new AtomicBoolean(false);
    }

    @MThread("listener")
    public void acceptTCPPacket(TCPPacket packet) {
        //  Ignore 6 byte packets - network noise
        if (packet.payload.length == 6) {
            return;
        }
        if (startingSeqNumber == -1L) {
            startingSeqNumber = packet.seqNumber;
            currentSeqNumber = startingSeqNumber;
            nextExpectedSeqNumber = packet.nextExpectedSeqNumber();
            bytesRead = 0L;
            currentByteIndex = currentSeqNumber;
        }
        synchronized (notifyObject) {
            pendingPackets.add(packet);
            notifyObject.notifyAll();
            notifyAck.set(true);
        }
    }

    @MThread("user")
    private int getNextByte() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        while (workingPacket == null) {
            nextWorkingPacket();
        }
        int i = workingPacket.getByte(currentByteIndex);
        if (i >= 0) {
            currentByteIndex++;
            bytesRead++;
            return i;
        }
        //  End of stream
        if (i == -1) {
            //  Used to be we had a hole that was impossible but actually is EOS
            return -1;
        }
        //  Need next packet
        if (i == -2) {
            //  Get next and retry
            workingPacket = null;
            return getNextByte();
        }
        throw new IllegalStateException("Should not be able to hit here!");
    }

    @MThread("user")
    private void nextWorkingPacket() throws InterruptedException {
        workingPacket = pendingPackets.take();
        //  Retransmission
        if (currentSeqNumber == workingPacket.seqNumber) {
            nextExpectedSeqNumber = workingPacket.nextExpectedSeqNumber();
            return;
        }
        //  Hole
        if (nextExpectedSeqNumber < workingPacket.seqNumber) {
            pendingPackets.add(workingPacket);
            //  If a packet snuck in between us checking with isNextByteAvailableFromWorkingPacket, go ahead and get it,
            //  otherwise wait for it.
            if (!notifyAck.getAndSet(false)) {
                synchronized (notifyObject) {
                    notifyObject.wait();
                }
            }
            //  Take the new one.
            workingPacket = pendingPackets.take();

        } else if (nextExpectedSeqNumber > workingPacket.seqNumber) {
            //  Awkward overlap? Just get the next packet.

        } else {
            currentSeqNumber = workingPacket.seqNumber;
            nextExpectedSeqNumber = workingPacket.nextExpectedSeqNumber();
        }
    }

    @Override
    @MThread("user")
    public int read() throws IOException {
        try {
            return getNextByte();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}
