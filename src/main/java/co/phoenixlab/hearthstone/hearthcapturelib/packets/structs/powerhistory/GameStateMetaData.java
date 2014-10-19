package co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.powerhistory;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldNumber;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldType;

/**
 * Contains metadata.
 *
 * @author Vincent Zhang
 */
public class GameStateMetaData
        extends CaptureStruct {

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.INT)
    private int[] info;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.MetaType type;

    @FieldNumber(4)
    @FieldType(GameEnums.DataType.INT)
    private int data;

    public GameStateMetaData() {
        info = new int[0];
    }

    /**
     * TODO Gets a list of numbers that mean something...
     */
    public int[] getInfo() {
        return info;
    }

    /**
     * Gets the type of metadata.
     */
    public GameEnums.MetaType getType() {
        return type;
    }

    /**
     * Get the data value.
     */
    public int getData() {
        return data;
    }
}
