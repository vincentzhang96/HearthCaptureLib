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

package co.phoenixlab.hearthstone.hearthcapturelib.packets;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldNumber;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding.FieldType;

/**
 * Sent by the server after a client has made its choices in mulligan to set which cards the player now has. Server to Client only.
 *
 * @author Vincent Zhang
 */
public class Packet017MulliganResult extends CapturePacket {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT32)
    private int id;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.INT32)
    private int unknown1;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.BOOL)
    private boolean cancelable;

    @FieldNumber(4)
    @FieldType(GameEnums.DataType.INT32)
    private int countMin;

    @FieldNumber(5)
    @FieldType(GameEnums.DataType.INT32)
    private int countMax;

    @FieldNumber(6)
    @FieldType(GameEnums.DataType.INT32)
    private int[] entityIds;

    @FieldNumber(7)
    @FieldType(GameEnums.DataType.INT32)
    private int unknown2;


    public Packet017MulliganResult() {
        super();
    }

    /**
     * Get the ID of the entity performing the mulligan?
     */
    public int getId() {
        return id;
    }

    /**
     * Unknown. Seems to always be 1.
     */
    public int getUnknown1() {
        return unknown1;
    }

    /**
     * Whether or not this result can be cancelled.
     */
    public boolean isCancelable() {
        return cancelable;
    }

    /**
     * Get the minimum number of items that could have been chosen.
     */
    public int getCountMin() {
        return countMin;
    }

    /**
     * Get the maximum number of items that could have been chosen.
     */
    public int getCountMax() {
        return countMax;
    }

    /**
     * Get the IDs of the entities in the result set.
     */
    public int[] getEntityIds() {
        return entityIds;
    }

    /**
     * Unknown. Seems to always be 1.
     */
    public int getUnknown2() {
        return unknown2;
    }
}
