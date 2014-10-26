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

package co.phoenixlab.hearthstone.hearthcapturelib.util;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;

public class InstantTypeAdapterTest {

    private InstantTypeAdapter typeAdapter;

    @Before
    public void before() {
        typeAdapter = new InstantTypeAdapter();
    }


    @Test
    public void testWrite() throws Exception {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter(stringWriter);
        writer.beginObject();
        writer.name("test");
        Instant instant = Instant.ofEpochMilli(0);
        typeAdapter.write(writer, instant);
        writer.endObject();
        writer.flush();
        String result = stringWriter.toString();
        String expected = String.format("{\"test\":\"%s\"}", new SimpleDateFormat("YYMMdd HH:mm:ss:SSS").format(new Date(0)));
        assertEquals(expected, result);
    }

    @Test
    public void testRead() throws Exception {
        Instant now = Instant.now();
        StringReader stringReader = new StringReader(String.format("{\"test\":\"%s\"}", new SimpleDateFormat("YYMMdd HH:mm:ss:SSS").format(new Date(now.toEpochMilli()))));
        JsonReader reader = new JsonReader(stringReader);
        reader.beginObject();
        reader.nextName();
        Instant instant = typeAdapter.read(reader);
        reader.endObject();
        assertEquals(now.toEpochMilli(), instant.toEpochMilli());
    }
}
