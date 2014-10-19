package co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.powerhistory;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldNumber;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldType;

/**
 * A changed tag property.
 *
 * @author Vincent Zhang
 */
public class GameStateTagUpdate
        extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT)
    private int entity;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.GameTag tag;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.INT)
    private int value;

    /**
     * The target entity of this change.
     */
    public int getEntity() {
        return entity;
    }

    /**
     * Get the tag that changed.
     */
    public GameEnums.GameTag getTag() {
        return tag;
    }

    /**
     * Get the new value of the tag that changed.
     */
    public int getValue() {
        return value;
    }
}
