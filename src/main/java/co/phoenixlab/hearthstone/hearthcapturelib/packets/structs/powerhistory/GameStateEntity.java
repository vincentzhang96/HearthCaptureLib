package co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.powerhistory;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldNumber;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldType;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.StructTag;

/**
 * State of an entity in game.
 *
 * @author Vincent Zhang
 */
public class GameStateEntity
        extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT32)
    private int entity;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.STRING)
    private String name;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructTag[] tags;


    public GameStateEntity() {
        tags = new StructTag[0];
        name = "";
    }

    /**
     * Get the id of the entity in question.
     */
    public int getEntity() {
        return entity;
    }

    /**
     * Get the internal name of the entity (eg CS2_123).
     */
    public String getInternalName() {
        return name;
    }

    /**
     * Get the tags describing the entity.
     */
    public StructTag[] getTags() {
        return tags;
    }
}
