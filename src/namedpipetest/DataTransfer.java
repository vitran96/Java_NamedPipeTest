package namedpipetest;

import com.sun.jna.platform.win32.WinNT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DataTransfer {

//    private WinNT.HANDLE pipe;

    private RandomAccessFile file;
    private static final Object lock = new Object();

    public DataTransfer(WinNT.HANDLE pipe) {
//        this.pipe = pipe;
    }

    @Override
    protected void finalize() throws Throwable {
        file.close();
        super.finalize();
    }

    public DataTransfer(String pipeName) throws FileNotFoundException {
        this.file = new RandomAccessFile(pipeName, "rw");
    }

    public void SendData(byte[] bytes) throws IOException {
//        synchronized (pipe) {
//            IntByReference lpNumberOfBytesRead = new IntByReference(0);
//            Kernel32.INSTANCE.WriteFile(
//                pipe,
//                ByteUtil.toByteArrayWithLittleEndian(bytes.length),
//                4,
//                lpNumberOfBytesRead,
//                null
//            );
//
//            if (bytes.length > 0) {
//                Kernel32.INSTANCE.WriteFile(
//                    pipe,
//                    bytes,
//                    bytes.length,
//                    lpNumberOfBytesRead,
//                    null
//                );
//            }
//        }

        synchronized (lock) {
            byte[] lengthInBytes = ByteUtil.intToBytes(bytes.length);

            byte[] dataToSend = new byte[lengthInBytes.length + bytes.length];
            System.arraycopy(lengthInBytes, 0, dataToSend, 0, lengthInBytes.length);
            System.arraycopy(bytes, 0, dataToSend, lengthInBytes.length, bytes.length);

            file.write(dataToSend);
        }
    }

    public byte[] ReceiveData() throws IOException {
//        synchronized (pipe) {
//            IntByReference lpNumberOfBytesRead = new IntByReference(0);
//            byte[] lenBuff = new byte[4];
//            Kernel32.INSTANCE.ReadFile(
//                pipe,
//                lenBuff,
//                lenBuff.length,
//                lpNumberOfBytesRead,
//                null
//            );
//
//            int length = ByteUtil.getIntFromArrayWithLittleEndian(lenBuff);
//            byte[] buff = new byte[length];
//            if (length > 0) {
//                Kernel32.INSTANCE.ReadFile(pipe, buff, buff.length, lpNumberOfBytesRead, null);
//            }
//            return buff;
//        }
        byte[] result;
        synchronized (lock) {
            byte[] lengthInBytes = new byte[4];
            file.read(lengthInBytes, 0, lengthInBytes.length);

            int length = ByteUtil.getIntFromArrayWithLittleEndian(lengthInBytes);
            byte[] receiveData = new byte[length];
            file.read(receiveData, 0, length);
            result = receiveData;
        }
        return result;
    }

}
