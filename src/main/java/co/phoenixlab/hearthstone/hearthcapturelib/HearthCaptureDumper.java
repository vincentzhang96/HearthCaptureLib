package co.phoenixlab.hearthstone.hearthcapturelib;

import co.phoenixlab.hearthstone.hearthcapturelib.packets.CapturePacket;
import co.phoenixlab.hearthstone.hearthcapturelib.util.InstantTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static java.nio.file.StandardOpenOption.*;

/**
 * Dumps captures to file.
 */
public class HearthCaptureDumper implements Closeable {

    private final BufferedWriter writer;
    private final Object writeLock;
    private final Gson gson;
    private CyclicBarrier cyclicBarrier;
    private CaptureQueue queue;

    public HearthCaptureDumper(Path dumpFile) throws IOException {
        writer = Files.newBufferedWriter(dumpFile, StandardCharsets.UTF_8, WRITE, TRUNCATE_EXISTING, CREATE);
        writeLock = new Object();
        gson = new GsonBuilder().
                registerTypeAdapter(Instant.class, new InstantTypeAdapter()).
                create();
    }

    public void dump(CaptureQueue queue) throws InterruptedException, IOException {
        this.queue = queue;
        Objects.requireNonNull(queue, "CaptureQueue cannot be null.");
        if (queue.isClosed()) {
            throw new IllegalStateException("CaptureQueue cannot be closed when calling dump()!");
        }
        cyclicBarrier = new CyclicBarrier(3);
        HearthCaptureLib.executor.execute(() -> run(true));
        HearthCaptureLib.executor.execute(() -> run(false));
        try {
            cyclicBarrier.await();
            cyclicBarrier.reset();
            cyclicBarrier.await();
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        writer.flush();
        System.out.println("Finished");
    }

    private void run(boolean inbound) {
        try {
            PacketQueue packetQueue = (inbound) ? queue.getInboundPackets() : queue.getOutboundPackets();
            cyclicBarrier.await();
            while (!queue.isClosed()) {
                if(!writePacket(packetQueue.next())) {
                    break;
                }
            }
        } catch (InterruptedException | BrokenBarrierException ignored) {
        } catch (IOException e) {
            System.err.println("Error while writing packets.");
            e.printStackTrace(System.err);
        } finally {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException ignored) {
            }
            System.out.println("Finished " + (inbound ? "inbound" : "outbound") + " packet read");
        }
    }

    private boolean writePacket(CapturePacket packet) throws IOException {
        if(packet == null) {
            return false;
        }
        synchronized (writeLock) {
            writer.write(packet.toJSON(gson));
            writer.write(System.lineSeparator());
        }
        return true;
    }


    @Override
    public void close() throws IOException {
        writer.close();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                Path path = Paths.get(args[0]);
                try (HearthCaptureDumper dumper = new HearthCaptureDumper(path)) {
                    HearthCaptureLib captureLib = new HearthCaptureLib();
                    System.out.println("Dumping...");
                    dumper.dump(captureLib.listen());
                    System.out.println("Dump complete!");
                }
            } catch (InvalidPathException e) {
                System.err.println("Invalid path specified: " + e.getLocalizedMessage());
            } catch (IOException e) {
                System.err.println("Unable to open file for dumping: ");
                e.printStackTrace(System.err);
            } catch (InterruptedException e) {
                System.err.println("Program interrupted.");
            }
        } else {
            System.err.println("Usage: java -cp HearthCaptureLib.jar co.phoenixlab.hearthstone.hearthcapturelib.CaptureDumper FILE_TO_DUMP_TO");
        }
        System.out.println("Application terminated.");
    }
}
