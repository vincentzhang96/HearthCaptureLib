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

import co.phoenixlab.hearthstone.hearthcapturelib.util.InstantTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;

/**
 * Abstract class that represents a data structure from a Hearthstone packet, including the packets themselves.
 *
 * @author Vincent Zhang
 */
public abstract class CaptureStruct {

    protected static final ThreadLocal<Gson> gson;

    static {
        gson = ThreadLocal.withInitial(() -> new GsonBuilder().
                setPrettyPrinting().
                registerTypeAdapter(Instant.class, new InstantTypeAdapter()).
                create());
    }

    public final String _structName;


    public CaptureStruct() {
        _structName = getClass().getSimpleName();
    }


    public String toJSON() {
        return toJSON(gson.get());
    }

    public String toJSON(Gson g) {
        return g.toJson(this, getClass());
    }

    public void postRead() {

    }
}
