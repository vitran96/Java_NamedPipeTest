package namedpipetest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public final class ByteUtil {
    public static byte[] toByteArrayWithLittleEndian(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);// default is BIG_ENDIAN
        buffer.putInt(value);
        buffer.flip();
        return buffer.array();
    }

    public static int getIntFromArrayWithLittleEndian(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }

    public static String Bytes2String(byte[] bytes) {
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    public static byte[] String2Bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }
}
