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
import co.phoenixlab.hearthstone.hearthcapturelib.util.HCapUtils;
import co.phoenixlab.hearthstone.hearthcapturelib.util.InstantTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * Reads packet dumps that were dumped by HearthCaptureDumper
 */
public class HearthCaptureDumpReader implements AutoCloseable {

    static class SignalPacket extends CapturePacket {

    }

    private static final SignalPacket SIGNAL_PACKET = new SignalPacket();

    private static int version = 1;

    private final BufferedReader reader;
    private final Gson gson;
    private CyclicBarrier cyclicBarrier;
    private CaptureQueue queue;
    private DumpedPacketQueue outQueue;
    private DumpedPacketQueue inQueue;
    private long startTime;

    public HearthCaptureDumpReader(Path dumpFile) throws IOException {
        reader = Files.newBufferedReader(dumpFile, StandardCharsets.UTF_8);
        gson = new GsonBuilder().
                registerTypeAdapter(Instant.class, new InstantTypeAdapter()).
                create();
    }

    /**
     * Reads a dump file header, starts the async parsing, and returns the CaptureQueue that receives the read packets.
     *
     * @return A CaptureQueue that receives the packets read from disk.
     * @throws IOException If there was an error reading or if the file is invalid.
     */
    public CaptureQueue read() throws IOException {
        String line = reader.readLine();
        if (!line.startsWith("HCLDMP ")) {
            throw new IOException("Invalid HearthCaptureLib dump: Invalid magic");
        }
        line = line.substring("HCLDMP ".length()).trim();
        int vers = -1;
        try {
            vers = Integer.parseInt(line);
        } catch (NumberFormatException nfe) {
            throw new IOException("Invalid HearthCaptureLib dump: Invalid version number: " + line);
        }
        if (vers != version) {
            throw new IOException("Unknown HCLDMP version " + vers);
        }
        line = reader.readLine();
        if (!line.startsWith("startTime ")) {
            throw new IOException("Invalid HearthCaptureLib dump: Invalid capture start timestamp");
        }
        line = line.substring("startTime ".length()).trim();
        startTime = -1L;
        try {
            startTime = Long.parseLong(line);
        } catch (NumberFormatException nfe) {
            throw new IOException("Invalid HearthCaptureLib dump: Invalid capture start timestamp " + line);
        }
        outQueue = new DumpedPacketQueue(startTime, true);
        inQueue = new DumpedPacketQueue(startTime, false);
        queue = new CaptureQueue(outQueue, inQueue);
        HearthCaptureLib.executor.execute(this::parse);
        return queue;
    }

    @SuppressWarnings("unchecked")
    private void parse() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (Thread.interrupted()) {
                    break;
                }
                CapturePacket packet = gson.fromJson(line, CapturePacket.class);
                boolean inbound = packet.isInbound();
                Class<? extends CapturePacket> clazz;
                try {
                    clazz = (Class<? extends CapturePacket>) Class.forName(packet._structName);
                } catch (ClassNotFoundException e) {
                    HCapUtils.logger.warning("Unknown type " + packet._structName);
                    continue;
                }
                packet = gson.fromJson(line, clazz);
                if (inbound) {
                    inQueue.put(packet);
                } else {
                    outQueue.put(packet);
                }
            }
        } catch (IOException e) {
            HCapUtils.logger.log(Level.WARNING, "Error reading from dump file", e);
        } catch (InterruptedException ignore) {
        } finally {
            queue.close();
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                Path path = Paths.get(args[0]);
                try (HearthCaptureDumpReader dumpReader = new HearthCaptureDumpReader(path)) {
                    HCapUtils.logger.info("Reading dump...");
                    final CaptureQueue queue = dumpReader.read();
                    CountDownLatch latch = new CountDownLatch(2);
                    new Thread(() -> {
                        try {
                            while (!queue.isClosed()) {
                                System.out.println(queue.getInboundPackets().next().toJSON());
                            }
                        } catch (InterruptedException ignored) {
                        }
                        latch.countDown();
                    }).start();
                    new Thread(() -> {
                        try {
                            while (!queue.isClosed()) {
                                System.err.println(queue.getOutboundPackets().next().toJSON());
                            }
                        } catch (InterruptedException ignored) {
                        }
                        latch.countDown();
                    }).start();
                    latch.await();
                    HCapUtils.logger.info("Read complete!");
                }
            } catch (InvalidPathException e) {
                HCapUtils.logger.severe("Invalid path specified: " + e.getLocalizedMessage());
            } catch (IOException e) {
                HCapUtils.logger.log(Level.SEVERE, "Unable to open file for reading.", e);
                e.printStackTrace();
            } catch (InterruptedException e) {
                HCapUtils.logger.warning("Program interrupted.");
            }
        } else {
            HCapUtils.logger.severe("Usage: java -cp HearthCaptureLib.jar co.phoenixlab.hearthstone.hearthcapturelib.CaptureDumpReader FILE_TO_DUMP_TO");
        }
        HCapUtils.logger.info("Application terminated.");
    }

    private class DumpedPacketQueue implements PacketQueue {

        private final ArrayBlockingQueue<CapturePacket> packets;
        private final boolean outbound;
        private final long startTime;
        private AtomicBoolean closed;

        private DumpedPacketQueue(long startTime, boolean outbound) {
            //  Large buffer because we're reading a dump from disk, as opposed to receiving them in real time.
            packets = new ArrayBlockingQueue<>(0xFFFF);
            this.startTime = startTime;
            this.outbound = outbound;
            closed = new AtomicBoolean(false);
        }

        @Override
        public CapturePacket next() throws InterruptedException {
            if (closed.get()) {
                return null;
            }
            CapturePacket packet = packets.take();
            if (packet == SIGNAL_PACKET) {
                throw new InterruptedException();
            }
            return packet;
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
            throw new UnsupportedOperationException();
        }

        public void put(CapturePacket packet) throws InterruptedException {
            packets.put(packet);
        }

        @Override
        public void close() {
            closed.set(true);
            try {
                packets.put(SIGNAL_PACKET);
            } catch (InterruptedException ignore) {
            }
        }

        @Override
        public long getCaptureStartTime() {
            return startTime;
        }
    }
}
