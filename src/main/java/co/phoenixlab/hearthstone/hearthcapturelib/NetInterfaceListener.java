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

import co.phoenixlab.hearthstone.hearthcapturelib.tcp.TCPAddressPortPair;
import co.phoenixlab.hearthstone.hearthcapturelib.tcp.TCPPacket;
import co.phoenixlab.hearthstone.hearthcapturelib.tcp.TCPStreamAssembler;
import co.phoenixlab.hearthstone.hearthcapturelib.util.HCapUtils;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * Captures packets from the network interface, filters out non-Hearthstone packets, and sends the remaining packets for parsing.
 *
 * @author Vincent Zhang
 */
public class NetInterfaceListener implements Runnable {

    private final PcapIf netInterface;
    private final Set<TCPAddressPortPair> blacklist;
    private final String cachedInterfaceDescription;
    private final CountDownLatch latch;
    private final AtomicReference<CaptureQueue> result;
    private final boolean logRawPackets;
    private Pcap pcap;

    public NetInterfaceListener(PcapIf netInterface, CountDownLatch latch, AtomicReference<CaptureQueue> result) {
        this.netInterface = netInterface;
        blacklist = new HashSet<>();
        cachedInterfaceDescription = Optional.ofNullable(netInterface.getDescription()).orElse("<no desc>");
        this.result = result;
        this.latch = latch;
        logRawPackets = !"false".equalsIgnoreCase(System.getProperty("phoenixlab.hearthstone.lograw", "false"));
    }

    private String logString(String s) {
        return netInterface.getName() + "/" + cachedInterfaceDescription + ": " + s;
    }

    @Override
    public void run() {
        try {
            HCapUtils.logger.info(logString("Starting..."));
            setUp();
            listen();
        } catch (Exception e) {
            HCapUtils.logger.log(Level.SEVERE, logString("Exception during listen"), e);
        }
    }

    private void setUp() throws Exception {
        //  Open the device
        StringBuilder errorBuilder = new StringBuilder();
        pcap = Pcap.openLive(netInterface.getName(), 65535, Pcap.MODE_NON_PROMISCUOUS, 1_000, errorBuilder);
        if (pcap == null || errorBuilder.length() != 0) {
            String err = errorBuilder.toString();
            HCapUtils.logger.severe(logString(String.format("Failed to open: %s",
                                                            err)));
            throw new IOException(err);
        }
        //  Compile filter
        PcapBpfProgram filter = new PcapBpfProgram();
        if (pcap.compile(filter, "tcp port 3724 or tcp port 1119", 1, 0) == -1) {
            String err = pcap.getErr();
            HCapUtils.logger.severe(logString(String.format("Failed to compile filter on: %s", err)));
            throw new IOException(err);
        }
        //  Set filter
        if (pcap.setFilter(filter) == -1) {
            String err = pcap.getErr();
            HCapUtils.logger.severe(logString(String.format("Failed to set filter: %s", err)));
            throw new IOException(err);
        }
    }

    private void listen() {
        //  Start reading
        PcapPacket pcapPacket = new PcapPacket(JMemory.Type.POINTER);
        HCapUtils.logger.info(logString("Read start"));
        try {
            while (true) {
                if (result.get() != null) {
                    break;
                }
                if (pcap.nextEx(pcapPacket) != Pcap.NEXT_EX_OK) {
                    continue;
                }
                PcapPacket copy = new PcapPacket(pcapPacket);
                TCPPacket packet = new TCPPacket(copy);
                TCPAddressPortPair remoteAddress = packet.connectionInfo.getRemoteAddress();
                //  FIN - Clear from blacklist (since the next connection on that address might be valid
                if ((packet.tcpFlags & 0x01) != 0) {
                    blacklist.remove(remoteAddress);
                    continue;
                }
                if (blacklist.contains(remoteAddress)) {
                    continue;
                }
                if (packet.payload.length == 0) {
                    HCapUtils.logger.info(logString("Skipping 0 len " + packet.connectionInfo.toString()));
                    continue;
                }
                //  Check for Aurora first byte
                if (packet.payload[0] == (byte) 0xA8) {
                    final TCPAddressPortPair serverAddress = remoteAddress;
                    HCapUtils.logger.info(logString("Got Aurora handshake"));
                    final TCPStreamAssembler inboundAssembler = new TCPStreamAssembler();
                    final TCPStreamAssembler outboundAssembler = new TCPStreamAssembler();
                    long startTime = System.currentTimeMillis();
                    final HearthPacketQueue inboundQueue = new HearthPacketQueue(inboundAssembler, false, startTime);
                    final HearthPacketQueue outboundQueue = new HearthPacketQueue(outboundAssembler, true, startTime);
                    final CaptureQueue captureQueue = new CaptureQueue(outboundQueue, inboundQueue);
                    //  DEBUG DUMPING
                    DebugDumper outDumper = null;
                    DebugDumper inDumper = null;
                    if (result.compareAndSet(null, captureQueue)) {
                        //  OK!
                        latch.countDown();
                        //  Handle our Aurora Handshake
                        outboundQueue.put(packet);
                        HearthCaptureLib.executor.execute(inboundQueue::parseLoop);
                        HearthCaptureLib.executor.execute(outboundQueue::parseLoop);
                        if (logRawPackets) {
                            outDumper = new DebugDumper(Paths.get("txt/dump-" + remoteAddress.ip + " " + remoteAddress.port + "-OUT"));
                            inDumper = new DebugDumper(Paths.get("txt/dump-" + remoteAddress.ip + " " + remoteAddress.port + "-IN"));
                            outDumper.writeRawPacketToDump(packet);
                        }
                        //  PACKET LOOP
                        try {
                            while (true) {
                                if (pcap.nextEx(pcapPacket) != Pcap.NEXT_EX_OK) {
                                    continue;
                                }
                                copy = new PcapPacket(pcapPacket);
                                TCPPacket tcpPacket = new TCPPacket(copy);
                                remoteAddress = tcpPacket.connectionInfo.getRemoteAddress();
                                if (!serverAddress.equals(remoteAddress)) {
                                    continue;
                                }
                                if (tcpPacket.connectionInfo.isDestinationRemote()) {
                                    //  DEBUG DUMPING
                                    if (outDumper != null) {
                                        outDumper.writeRawPacketToDump(tcpPacket);
                                    }
                                    outboundQueue.put(tcpPacket);
                                } else if (tcpPacket.connectionInfo.isSourceRemote()) {
                                    //  DEBUG DUMPING
                                    if (inDumper != null) {
                                        inDumper.writeRawPacketToDump(tcpPacket);
                                    }
                                    inboundQueue.put(tcpPacket);
                                }
                                //  FIN
                                if ((tcpPacket.tcpFlags & 0x01) != 0) {
                                    HCapUtils.logger.info(logString("End of stream - FIN"));
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            HCapUtils.logger.log(Level.SEVERE, logString("Critical inner error"), e);
                        } finally {
                            if (outDumper != null) {
                                outDumper.close();
                            }
                            if (inDumper != null) {
                                inDumper.close();
                            }
                            captureQueue.close();
                        }
                    } else {
                        HCapUtils.logger.severe(logString("Other thread beat us or there are multiple Hearthstone clients open?"));
                        return;
                    }
                } else {
                    HCapUtils.logger.info(logString("Not an Aurora :( Blacklisting " + remoteAddress.toString()));
                    blacklist.add(remoteAddress);
                }
            }
        } catch (Exception e) {
            HCapUtils.logger.log(Level.SEVERE, logString("Exception in packet reading."), e);
        }
        HCapUtils.logger.info(logString("Finished"));
    }


}

class DebugDumper {

    private final BufferedWriter writer;

    DebugDumper(Path target) throws IOException {
        writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        writer.write("\n\n");
        writer.write("---- NEW DUMP @ " + new Date().toString() + " ----\n\n");
        writer.flush();
    }

    void writeRawPacketToDump(TCPPacket packet) throws IOException {
        writer.write(String.format("SEQ: %08X NXT: %08X LEN: %08X FLG: %08X%n", packet.seqNumber, packet.nextExpectedSeqNumber(), packet.payload.length, packet.tcpFlags));
        writer.write(FormatUtils.hexdump(packet.payload));
        writer.flush();
    }

    void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
