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

package co.phoenixlab.hearthstone.hearthcapturelib.packets.encoding;

import co.phoenixlab.hearthstone.hearthcapturelib.GameEnums;
import co.phoenixlab.hearthstone.hearthcapturelib.packets.CaptureStruct;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Packet decoder.
 *
 * @author Vincent Zhang
 */
@SuppressWarnings("unchecked")
public class HSDecoder {

    /**
     * Decodes a CaptureStruct from the given buffer.
     * @param buffer A ByteBuffer containing the raw bytes.
     * @param clazz The target CaptureStruct.
     * @param <T> The subtype of CaptureStruct.
     * @throws IOException If there was an error reading the struct.
     */
    public static <T extends CaptureStruct> T decode(ByteBuffer buffer, Class<? extends CaptureStruct> clazz) throws IOException {
        Field[] fields = clazz.getDeclaredFields();
        HashMap<Integer, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            FieldNumber order = field.getAnnotation(FieldNumber.class);
            if (order != null) {
                fieldMap.put(order.value(), field);
            }
        }
        T ret;
        try {
            ret = (T) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Unable to instantiate CapturePacket " + clazz.getName(), e);
        }
        HashMap<Field, List> workingArrays = new HashMap<>();
        while (!fieldMap.isEmpty() && buffer.remaining() > 0) {
            processNextField(buffer, clazz, fieldMap, ret, workingArrays);
        }
        //  Process our arrays
        for (Map.Entry<Field, List> entry : workingArrays.entrySet()) {
            try {
                handleArray(ret, entry);
            } catch (Exception e) {
                throw new RuntimeException("Error processing arrays for " + clazz.getName() + " for field " + entry.getKey().getName(), e);
            }
        }
        ret.postRead();
        return ret;
    }

    private static <T extends CaptureStruct> void processNextField(ByteBuffer buffer, Class<? extends CaptureStruct> clazz, HashMap<Integer, Field> fieldMap, T ret, HashMap<Field, List> workingArrays) throws
            IOException {
        int i = Byte.toUnsignedInt(buffer.get());
        int fieldNumber = i >> 3;
        int type = i & 0x07;
        Field field = fieldMap.get(fieldNumber);
        if (field == null) {
            throw new IOException("Unknown field " + fieldNumber + " in " + clazz.getName() + ", " + fieldMap.size() + " remaining " + fieldMap.toString() + "\n contents " + ret.toJSON());
        }
        String fieldName = field.getName();
        field.setAccessible(true);
        FieldType fType = field.getAnnotation(FieldType.class);
        if (fType == null) {
            throw new IOException("Missing field type for " + field.getDeclaringClass().getName() + "#" + fieldName);
        }
        GameEnums.DataType dataType = fType.value();
        boolean isArray = field.getType().isArray();
        if (!isArray) {
            fieldMap.remove(fieldNumber);
        }
        List list = null;
        if (isArray) {
            list = workingArrays.get(field);
            if (list == null) {
                list = new ArrayList<>();
                workingArrays.put(field, list);
            }
        }
        try {
            switch (dataType) {
                case STRING: {
                    long length = readUnsignedVarInt(buffer);
                    byte[] data = new byte[(int) length];
                    buffer.get(data);
                    String s = new String(data, StandardCharsets.UTF_8);
                    if (isArray) {
                        list.add(s);
                    } else {
                        field.set(ret, s);
                    }
                }
                break;
                case BYTES: {
                    long length = readUnsignedVarInt(buffer);
                    byte[] data = new byte[(int) length];
                    buffer.get(data);
                    if (isArray) {
                        list.add(data);
                    } else {
                        field.set(ret, data);
                    }
                }
                break;
                case STRUCT: {
                    long length = readUnsignedVarInt(buffer);
                    byte[] data = new byte[(int) length];
                    buffer.get(data);
                    StructHandler handler = field.getAnnotation(StructHandler.class);
                    Class<? extends CaptureStruct> handlerClazz;
                    if (handler == null || handler.value() == null) {
                        if (field.getType().isArray()) {
                            handlerClazz = (Class<? extends CaptureStruct>) field.getType().getComponentType();
                        } else {
                            handlerClazz = (Class<? extends CaptureStruct>) field.getType();
                        }
                    } else {
                        handlerClazz = handler.value();
                    }
                    ByteBuffer dataBuffer = ByteBuffer.allocate((int) length);
                    dataBuffer.put(data);
                    dataBuffer.flip();
                    dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    CaptureStruct packet = HSDecoder.decode(dataBuffer, handlerClazz);
                    if (isArray) {
                        assert list != null;
                        list.add(packet);
                    } else {
                        field.set(ret, packet);
                    }
                }
                break;
                case INT:
                case INT32: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(readUnsignedVarInt(buffer));
                        }
                    } else {
                        field.setInt(ret, readUnsignedVarInt(buffer));
                    }
                }
                break;
                case UINT32: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(readSignedVarInt(buffer));
                        }
                    } else {
                        field.setInt(ret, readSignedVarInt(buffer));
                    }
                }
                break;
                case INT64: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(readSignedVarLong(buffer));
                        }
                    } else {
                        field.setLong(ret, readSignedVarLong(buffer));
                    }
                }
                break;
                case UINT64: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(readUnsignedVarLong(buffer));
                        }
                    } else {
                        field.setLong(ret, readUnsignedVarLong(buffer));
                    }
                }
                break;
                case BOOL: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(readSignedVarLong(buffer) != 0L);
                        }
                    } else {
                        field.setBoolean(ret, readSignedVarLong(buffer) != 0L);
                    }
                }
                break;
                case ENUM: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            int val = (int) readUnsignedVarLong(buffer);
                            Object enumVal = GameEnums.getById((Class<Enum>) field.getType(), val);
                            list.add(enumVal);
                        }
                    } else {
                        int val = (int) readUnsignedVarLong(buffer);
                        Object enumVal = GameEnums.getById((Class<Enum>) field.getType(), val);
                        field.set(ret, enumVal);
                    }
                }
                break;
                case FIXED32: {
                    if (isArray) {
                        int numElements = readUnsignedVarInt(buffer);
                        for (int c = 0; c < numElements; c++) {
                            list.add(buffer.getInt());
                        }
                    } else {
                        field.setInt(ret, buffer.getInt());
                    }
                }
                break;
                default: {
                    throw new IOException("Unknown type " + type);
                }
            }
        } catch (IOException e) {
            //  rethrow
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while parsing packet " + clazz.getName(), e);
        }
    }

    private static <T extends CaptureStruct> void handleArray(T ret, Map.Entry<Field, List> entry) throws IllegalAccessException {
        Field field = entry.getKey();
        List list = entry.getValue();
        Class<?> type = field.getType();
        if (!type.isArray()) {
            throw new IllegalArgumentException("Field " + field.getName() + " is not an array!");
        }
        Class<?> componentType = type.getComponentType();
        int size = list.size();
        //  For primative arrays, we unfortunately must explicitly create a primative array and populate it
        //  Field.set() does not like array objects created via Array.newInstance().
        if (componentType.isPrimitive()) {
            if (Integer.TYPE.equals(componentType)) {
                int[] arr = new int[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = (int) iter.next();
                }
                field.set(ret, arr);
            } else if (Long.TYPE.equals(componentType)) {
                long[] arr = new long[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = (long) iter.next();
                }
                field.set(ret, arr);
            } else if (Double.TYPE.equals(componentType)) {
                double[] arr = new double[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = (double) iter.next();
                }
                field.set(ret, arr);
            } else if (Float.TYPE.equals(componentType)) {
                float[] arr = new float[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = (float) iter.next();
                }
                field.set(ret, arr);
            } else if (Boolean.TYPE.equals(componentType)) {
                boolean[] arr = new boolean[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = (boolean) iter.next();
                }
                field.set(ret, arr);
            } else if (Byte.TYPE.equals(componentType)) {
                byte[] arr = new byte[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = (byte) iter.next();
                }
                field.set(ret, arr);
            } else if (Short.TYPE.equals(componentType)) {
                short[] arr = new short[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = (short) iter.next();
                }
                field.set(ret, arr);
            } else if (Character.TYPE.equals(componentType)) {
                char[] arr = new char[size];
                int i = 0;
                for (Iterator<Object> iter = list.iterator(); iter.hasNext(); i++) {
                    arr[i] = (char) iter.next();
                }
                field.set(ret, arr);
            }
        } else {
            Class arrayClass = Array.newInstance(componentType, 0).getClass();
            field.set(ret, list.toArray(Arrays.copyOf(new Object[size], size, arrayClass)));
        }
    }

    private static long readSignedVarLong(ByteBuffer buffer) throws IOException {
//        long raw = readUnsignedVarInt(buffer);
//        long temp = (((raw << 63) >> 63) ^ raw) >> 1;
//        return temp ^ (raw & (1L << 63));
        //  TODO So far we haven't seen any actual negative numbers in packets, so it's possible they don't actually differentiate between signed and unsigned.
        return readUnsignedVarLong(buffer);
    }

    private static long readUnsignedVarLong(ByteBuffer buffer) {
        long value = 0L;
        int i = 0;
        long b;
        while (((b = Byte.toUnsignedInt(buffer.get())) & 0x80L) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | (b << i);
    }

    private static int readSignedVarInt(ByteBuffer buffer) throws IOException {
//        int raw = readUnsignedVarInt(buffer);
//        int temp = (((raw << 31) >> 31) ^ raw) >> 1;
//        return temp ^ (raw & (1 << 31));
        //  TODO So far we haven't seen any actual negative numbers in packets, so it's possible they don't actually differentiate between signed and unsigned.
        return readUnsignedVarInt(buffer);
    }

    private static int readUnsignedVarInt(ByteBuffer buffer) {
        int value = 0;
        int i = 0;
        int b;
        while (((b = Byte.toUnsignedInt(buffer.get())) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | (b << i);
    }
}
