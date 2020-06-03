package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

public class DataTransfer {

    private WinNT.HANDLE pipe;

    public DataTransfer(WinNT.HANDLE pipe) {
        this.pipe = pipe;
    }

    public void SendData(byte[] bytes) {
        synchronized (pipe) {
            IntByReference lpNumberOfBytesRead = new IntByReference(0);
            Kernel32.INSTANCE.WriteFile(
                pipe,
                ByteUtil.toByteArrayWithLittleEndian(bytes.length),
                4,
                lpNumberOfBytesRead,
                null
            );

            if (bytes.length > 0) {
                Kernel32.INSTANCE.WriteFile(
                    pipe,
                    bytes,
                    bytes.length,
                    lpNumberOfBytesRead,
                    null
                );
            }
        }
    }

    public byte[] ReceiveData() {
        synchronized (pipe) {
            IntByReference lpNumberOfBytesRead = new IntByReference(0);
            byte[] lenBuff = new byte[4];
            Kernel32.INSTANCE.ReadFile(
                pipe,
                lenBuff,
                lenBuff.length,
                lpNumberOfBytesRead,
                null
            );

            int length = ByteUtil.getIntFromArrayWithLittleEndian(lenBuff);
            byte[] buff = new byte[length];
            if (length > 0) {
                Kernel32.INSTANCE.ReadFile(pipe, buff, buff.length, lpNumberOfBytesRead, null);
            }
            return buff;
        }
    }

}
