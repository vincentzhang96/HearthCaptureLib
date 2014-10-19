package co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.powerhistory;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldNumber;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldType;

/**
 * Contains info on a hidden entityId.
 *
 * @author Vincent Zhang
 */
public class GameStateHiddenEntity
        extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT32)
    private int entityId;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.Zone zone;

    /**
     * Get the ID of the entity.
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * Get which zone the entity belongs to.
     */
    public GameEnums.Zone getZone() {
        return zone;
    }
}
