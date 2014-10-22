package co.phoenixlab.hearthstone.hearthcapturelib.packets;

import java.time.Instant;

/**
 * Represents a top level packet.
 *
 * @author Vincent Zhang
 */
public class CapturePacket extends CaptureStruct {

    /**
     * The Instant that this packet was decoded.
     */
    public final Instant decodeTime = Instant.now();
    /**
     * The number of milliseconds since the beginning of the capture session.
     */
    private long captureDeltaTime;
    /**
     * Whether or not this packet is an inbound packet or not (outbound).
     */
    private boolean inbound;

    public CapturePacket setInbound(boolean inbound) {
        this.inbound = inbound;
        return this;
    }

    /**
     * Gets whether or not this packet is an inbound packet or not.
     * @return true if this packet is inbound or false if this packet is outbound.
     */
    public boolean isInbound() {
        return inbound;
    }

    /**
     * Gets the number of milliseconds since the beginning of the capture session.
     */
    public long getCaptureDeltaTime() {
        return captureDeltaTime;
    }

    public CapturePacket setCaptureDeltaTime(long captureDeltaTime) {
        this.captureDeltaTime = captureDeltaTime;
        return this;
    }
}
