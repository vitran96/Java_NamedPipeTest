package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataTransfer {

    private WinNT.HANDLE pipe;

    public DataTransfer(WinNT.HANDLE pipe) {
        this.pipe = pipe;
    }

    public void SendData(byte[] bytes) {
        synchronized (pipe) {
            IntByReference lpNumberOfBytesRead = new IntByReference(0);
            Kernel32.INSTANCE.WriteFile(pipe, toByteArrayWithLittleEndian(bytes.length), 4, lpNumberOfBytesRead, null);
            if (bytes.length > 0) {
                Kernel32.INSTANCE.WriteFile(pipe, bytes, bytes.length, lpNumberOfBytesRead, null);
            }
        }
    }

    public byte[] ReceiveData() {
        synchronized (pipe) {
            IntByReference lpNumberOfBytesRead = new IntByReference(0);
            byte[] lenBuff = new byte[4];
            Kernel32.INSTANCE.ReadFile(pipe, lenBuff, lenBuff.length, lpNumberOfBytesRead, null);
            int length = getIntFromArrayWithLittleEndian(lenBuff);
            byte[] buff = new byte[length];
            if (length > 0) {
                Kernel32.INSTANCE.ReadFile(pipe, buff, buff.length, lpNumberOfBytesRead, null);
            }
            return buff;
        }
    }

    private static byte[] toByteArrayWithLittleEndian(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);// default is BIG_ENDIAN
        buffer.putInt(value);
        buffer.flip();
        return buffer.array();
    }

    private static int getIntFromArrayWithLittleEndian(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }
}
