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
 * Represents a choice (and possibly sub-choices) that a player can make, for instance attack an enemy minion with your minion.
 *
 * @author Vincent Zhang
 */
public class StructOption extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.ENUM)
    private GameEnums.OptionType type;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructSubOption mainOption;

    @FieldNumber(3)
    @FieldType(GameEnums.DataType.STRUCT)
    private StructSubOption[] subOptions;

    public StructOption() {
        super();
        subOptions = new StructSubOption[0];
    }

    /**
     * Get the type of action that this option performs.
     */
    public GameEnums.OptionType getType() {
        return type;
    }

    /**
     * Get the main choice for this option, which is the primary action that can be taken (and its targets).
     */
    public StructSubOption getMainOption() {
        return mainOption;
    }

    /**
     * Gets all other side options (such as battlecries, secondary damage targets, etc).
     */
    public StructSubOption[] getSubOptions() {
        return subOptions;
    }
}
