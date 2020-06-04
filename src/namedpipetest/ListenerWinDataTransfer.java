package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;

public class ListenerWinDataTransfer implements DataTransfer {

    private WinNT.HANDLE pipe;

    public ListenerWinDataTransfer(WinNT.HANDLE pipe) {
        this.pipe = pipe;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public void SendData(byte[] bytes) {
        synchronized (pipe) {
            IntByReference lpNumberOfBytesRead = new IntByReference(0);
            Kernel32.INSTANCE.WriteFile(
                pipe,
                ByteUtil.intToBytes(bytes.length),
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

    @Override
    public byte[] ReceiveData() {
        byte[] buff;
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
            buff = new byte[length];
            if (length > 0) {
                Kernel32.INSTANCE.ReadFile(
                    pipe,
                    buff,
                    buff.length,
                    lpNumberOfBytesRead,
                    null
                );
            }
        }

        return buff;
    }

}
