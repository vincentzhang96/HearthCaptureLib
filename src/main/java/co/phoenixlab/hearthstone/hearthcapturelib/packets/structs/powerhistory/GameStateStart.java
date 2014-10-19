package co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.powerhistory;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldNumber;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldType;

/**
 * Indicates the beginning of a game state block.
 *
 * @author Vincent Zhang
 */
public class GameStateStart extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.ActionSubType type;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.INT)
    private int index;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.INT)
    private int source;

    @FieldNumber(4)
    @FieldType(GameEnums.DataType.INT)
    private int target;

    public GameEnums.ActionSubType getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public int getSource() {
        return source;
    }

    /**
     * The ID of the target entity of the event.
     */
    public int getTarget() {
        return target;
    }
}
