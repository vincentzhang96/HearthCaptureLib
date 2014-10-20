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
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.HSDecoder;
import co.phoenixlab.hearthstone.hearthcapturelib.tcp.TCPPacket;
import co.phoenixlab.hearthstone.hearthcapturelib.tcp.TCPStreamAssembler;
import co.phoenixlab.hearthstone.hearthcapturelib.util.HCapUtils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implements a PacketQueue for Hearthstone packets.
 *
 * @author Vincent Zhang
 */
class HearthPacketQueue
        implements PacketQueue {

    static class SignalPacket extends CapturePacket {

    }

    private static final SignalPacket SIGNAL_PACKET = new SignalPacket();

    private final TCPStreamAssembler assembler;
    private final DataInputStream inputStream;
    private final AtomicBoolean closed;
    private final ArrayBlockingQueue<CapturePacket> packets;
    private final boolean outbound;

    public HearthPacketQueue(TCPStreamAssembler assembler, boolean outbound) {
        this.assembler = assembler;
        inputStream = new DataInputStream(assembler);
        packets = new ArrayBlockingQueue<>(1000);
        closed = new AtomicBoolean(false);
        this.outbound = outbound;
    }

    @Override
    public CapturePacket next() throws InterruptedException {
        if (closed.get()) {
            return null;
        }
        CapturePacket packet = packets.take();
        if(packet == SIGNAL_PACKET) {
            throw new InterruptedException();
        }
        return packet;
    }

    public void parseLoop() {
        while (!isClosed()) {
            try {
                CapturePacket packet = readPacket();
                if (packet != null) {
                    packets.put(packet);
                }
            } catch (EOFException eof) {
                break;
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private CapturePacket readPacket() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        byte[] data = new byte[4];
        inputStream.readFully(data);
        buffer.put(data);
        buffer.flip();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int packetId = buffer.getInt();
        //  If we have an invalid packetId drop the next 2 bytes
        //  (some sort of protocol noise?)
        if ((packetId & 0xFF) == 0) {
            inputStream.skipBytes(2);
            return null;
        }
        buffer.flip();
        inputStream.readFully(data);
        buffer.put(data);
        buffer.flip();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int length = buffer.getInt();
        ByteBuffer dataBuffer = ByteBuffer.allocate(length);
        data = new byte[length];
        inputStream.readFully(data);
        dataBuffer.put(data);
        dataBuffer.flip();
        dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        Class<? extends CaptureStruct> clazz = GameEnums.getById(GameEnums.PacketType.class, packetId).clazz;
        if (clazz == null) {
            HCapUtils.logger.warning("no packet for type " + packetId + (outbound ? " OUT" : " IN"));
            return null;
        }
        CapturePacket packet = HSDecoder.decode(dataBuffer, clazz);
        return packet.setInbound(!outbound);
    }

    @Override
    public CapturePacket peek() {
        if (closed.get()) {
            return null;
        }
        return packets.peek();
    }

    @Override
    public boolean hasNext() {
        return !closed.get() && packets.peek() != null;
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public void put(TCPPacket packet) {
        //  Filter out
        assembler.acceptTCPPacket(packet);
    }

    @Override
    public void close() {
        closed.set(true);
        try {
            packets.put(SIGNAL_PACKET);
        } catch (InterruptedException ignore) {
        }
    }
}
