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

package co.phoenixlab.hearthstone.hearthcapturelib.packets.structs;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldNumber;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldType;

/**
 * Represents a player. Each player is identified by a unique Blizzard ID. This struct also indicates which card back the player is using, as well as the
 * entity that represents the player.
 *
 * @author Vincent Zhang
 */
public class StructPlayer extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT32)
    private int id;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructBnetId bnetId;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.INT32)
    private int cardback;

    @FieldNumber(4)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructEntity entity;


    public StructPlayer() {
        super();
    }

    /**
     * Gets the player's ID in the game - 1 or 2.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the player's Battle.net unique ID.
     */
    public StructBnetId getBnetId() {
        return bnetId;
    }

    /**
     * Get which cardback ID the player is using.
     */
    public int getCardback() {
        return cardback;
    }

    /**
     * Get the entity that represents this player.
     */
    public StructEntity getEntity() {
        return entity;
    }
}
