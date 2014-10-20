package co.phoenixlab.hearthstone.hearthcapturelib.packets;

import java.time.Instant;

/**
 * Represents a top level packet.
 *
 * @author Vincent Zhang
 */
public class CapturePacket extends CaptureStruct {

    public final Instant decodeTime = Instant.now();
    private boolean inbound;

    public CapturePacket setInbound(boolean inbound) {
        this.inbound = inbound;
        return this;
    }

    public boolean isInbound() {
        return inbound;
    }
}
