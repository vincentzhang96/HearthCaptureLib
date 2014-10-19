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
 * Contains information about a player's mouse, namely (x,y) coordinates, which entity is being hovered over, and which entity the arrow should start from.
 *
 * @author Vincent Zhang
 */
public class StructMouseInfo extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.INT)
    private int arrowOrigin;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.INT)
    private int heldCard;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.INT)
    private int overCard;

    @FieldNumber(4)
    @FieldType(GameEnums.DataType.INT)
    private int x;

    @FieldNumber(5)
    @FieldType(GameEnums.DataType.INT)
    private int y;

    public StructMouseInfo() {
        super();
    }

    /**
     * Which entity the arrow should start from, or 0 if no arrow.
     */
    public int getArrowOrigin() {
        return arrowOrigin;
    }

    /**
     * Which card entity is currently being held in the air over the board, or 0 if not holding a card.
     */
    public int getHeldCard() {
        return heldCard;
    }

    /**
     * Which card is currently being hovered over by the player, or 0 if no hover.
     */
    public int getOverCard() {
        return overCard;
    }

    /**
     * Get the x coordinate (left/right) of the mouse cursor.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate (top/bottom) of the mouse cursor.
     */
    public int getY() {
        return y;
    }
}
