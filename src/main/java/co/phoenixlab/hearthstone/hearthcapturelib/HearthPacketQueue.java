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

    private final TCPStreamAssembler assembler;
    private final DataInputStream inputStream;
    private final AtomicBoolean closed;
    private final ArrayBlockingQueue<CaptureStruct> packets;
    private final boolean outbound;

    public HearthPacketQueue(TCPStreamAssembler assembler, boolean outbound) {
        this.assembler = assembler;
        inputStream = new DataInputStream(assembler);
        packets = new ArrayBlockingQueue<>(1000);
        closed = new AtomicBoolean(false);
        this.outbound = outbound;
    }

    @Override
    public CaptureStruct next() throws InterruptedException {
        if (closed.get()) {
            return null;
        }
        return packets.take();
    }

    public void parseLoop() {
        while (!isClosed()) {
            try {
                CaptureStruct packet = readPacket();
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

    private CaptureStruct readPacket() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        byte[] data = new byte[4];
        inputStream.readFully(data);
        buffer.put(data);
        buffer.flip();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int packetId = buffer.getInt();
//        System.out.println(String.format("next packet id %1$s %1$08X", packetId) + (outbound ? " OUT" : " IN"));
        //  If we have an invalid packetId drop the next 2 bytes
        //  (some sort of protocol noise?)
        if ((packetId & 0xFF) == 0) {
//            System.out.println("Skipping" + (outbound ? " OUT" : " IN"));
            inputStream.read();
            inputStream.read();
            return null;
        }
        buffer.flip();
        inputStream.readFully(data);
        buffer.put(data);
        buffer.flip();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int length = buffer.getInt();
        ByteBuffer dataBuffer = ByteBuffer.allocate(length);
//        System.out.println(String.format("next packet %1$s %1$08X bytes", length) + (outbound ? " OUT" : " IN"));
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
        //        HCapUtils.logger.info((outbound ? "OUT: " : " IN: ") + packet.toJSON());
        return HSDecoder.decode(dataBuffer, clazz);
    }

    @Override
    public CaptureStruct peek() {
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
    }
}
