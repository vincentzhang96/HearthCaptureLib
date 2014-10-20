package co.phoenixlab.hearthstone.hearthcapturelib.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Gson TypeAdapter for {@link java.time.Instant}.
 *
 * @author Vincent Zhang
 */
public class InstantTypeAdapter extends TypeAdapter<Instant> {

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");

    @Override
    public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
        if (instant == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(format.format(new Date(instant.toEpochMilli())));
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        try {
            return Instant.ofEpochMilli(format.parse(jsonReader.nextString()).getTime());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}
