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

import java.util.UUID;

/**
 * A Battle.net user ID. 64-bit UUID uniquely identifying the player.
 *
 * @author Vincent Zhang
 */
public class StructBnetId extends CaptureStruct {

    @FieldNumber(1)
    @FieldType(GameEnums.DataType.UINT64)
    private transient long lowBytes;

    @FieldNumber(2)
    @FieldType(GameEnums.DataType.UINT64)
    private transient long highBytes;

    private UUID uuid;

    public StructBnetId() {
        super();
    }

    /**
     * Returns the UUID
     */
    public UUID getUuid() {
        if (uuid == null) {
            uuid = new UUID(highBytes, lowBytes);
        }
        return uuid;
    }

    @Override
    public void postRead() {
        getUuid();
    }
}
