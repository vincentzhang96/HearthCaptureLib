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

import co.phoenixlab.hearthstone.hearthcapturelib.util.HCapUtils;
import co.phoenixlab.hearthstone.hearthcapturelib.util.NetInterfaces;
import org.jnetpcap.PcapIf;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The primary entry point of HearthCaptureLib. Provides the means to listen for a single Hearthstone game, returning a CaptureQueue
 * that allows a user to read game packets.
 * <p>A simple usage example that simply prints inbound packets to System.out and outbound to System.err:</p>
 * <pre>
 * {@code
     HearthCaptureLib hCL = new HearthCaptureLib();
        CaptureQueue capQueue = null;
        try {
            capQueue = hCL.listen();
        } catch (InterruptedException | NoSuchElementException e) {
            e.printStackTrace();
            return;
        }
        final CaptureQueue queue = capQueue;
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
 * }
 * </pre>
 * <p>The main method in this class has this as its implementation.</p>
 *
 * @author Vincent Zhang
 */
public class HearthCaptureLib implements HearthstoneCapturer {


    public static final Executor executor = Executors.newCachedThreadPool();

    public HearthCaptureLib() {

    }

    @Override
    public CaptureQueue listen() throws InterruptedException, NoSuchElementException {
        List<PcapIf> devices = NetInterfaces.getNetworkInterfaces();
        if (devices.isEmpty()) {
            throw new NoSuchElementException("No network interfaces found!");
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CaptureQueue> result = new AtomicReference<>();
        for (PcapIf device : devices) {
            NetInterfaceListener listener = new NetInterfaceListener(device, latch, result);
            executor.execute(listener);
        }
        HCapUtils.logger.info("Waiting on listeners...");
        latch.await();
        HCapUtils.logger.info("Listener found!");
        return result.get();
    }

    public static void main(String[] args) throws Exception {
        HearthCaptureLib hCL = new HearthCaptureLib();
        CaptureQueue capQueue = null;
        try {
            capQueue = hCL.listen();
        } catch (InterruptedException | NoSuchElementException e) {
            e.printStackTrace();
            return;
        }
        final CaptureQueue queue = capQueue;
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
    }
}
