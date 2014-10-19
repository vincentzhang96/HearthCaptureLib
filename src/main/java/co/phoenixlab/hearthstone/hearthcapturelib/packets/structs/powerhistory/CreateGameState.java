package co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.powerhistory;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldNumber;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldType;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.StructEntity;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.structs.StructPlayer;

/**
 * Contains information about the initial game state.
 *
 * @author Vincent Zhang
 */
public class CreateGameState
        extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructEntity gameEntity;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructPlayer[] players;

    public CreateGameState() {
        players = new StructPlayer[0];
    }

    /**
     * Gets the entity representing this game.
     */
    public StructEntity getGameEntity() {
        return gameEntity;
    }

    /**
     * Gets all the players in this game.
     */
    public StructPlayer[] getPlayers() {
        return players;
    }
}
