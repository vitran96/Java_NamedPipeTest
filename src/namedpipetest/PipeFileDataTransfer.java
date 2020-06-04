package namedpipetest;

import com.sun.jna.platform.win32.WinNT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PipeFileDataTransfer implements DataTransfer {

    private RandomAccessFile file;
    private final Object lock = new Object();

    @Override
    protected void finalize() throws Throwable {
        file.close();
        super.finalize();
    }

    public PipeFileDataTransfer(String pipeName) throws FileNotFoundException {
        this.file = new RandomAccessFile(pipeName, "rw");
    }

    @Override
    public void SendData(byte[] bytes) {
        try {
            synchronized (lock) {
                byte[] lengthInBytes = ByteUtil.intToBytes(bytes.length);

                byte[] dataToSend = new byte[lengthInBytes.length + bytes.length];
                System.arraycopy(lengthInBytes, 0, dataToSend, 0, lengthInBytes.length);
                System.arraycopy(bytes, 0, dataToSend, lengthInBytes.length, bytes.length);

                file.write(dataToSend);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] ReceiveData() {
        byte[] result = new byte[0];

        try {
            synchronized (lock) {
                byte[] lengthInBytes = new byte[4];
                file.read(lengthInBytes, 0, lengthInBytes.length);

                int length = ByteUtil.bytesToInt(lengthInBytes);
                byte[] receiveData = new byte[length];
                file.read(receiveData, 0, length);
                result = receiveData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
