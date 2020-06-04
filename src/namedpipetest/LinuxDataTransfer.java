package namedpipetest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class LinuxDataTransfer implements DataTransfer {

    private RandomAccessFile fileToSendTo;
    private RandomAccessFile fileToReceiveFrom;
    private final Object lock = new Object();

    @Override
    protected void finalize() throws Throwable {
        fileToSendTo.close();
        super.finalize();
    }

    public LinuxDataTransfer(String pipeNameToSendTo, String pipeNameToReceiveFrom) throws FileNotFoundException {
        this.fileToSendTo = new RandomAccessFile(pipeNameToSendTo, "rw");
        this.fileToReceiveFrom = new RandomAccessFile(pipeNameToReceiveFrom, "r");
    }

    @Override
    public void SendData(byte[] bytes) {
        try {
            synchronized (lock) {
                byte[] lengthInBytes = ByteUtil.intToBytes(bytes.length);

                byte[] dataToSend = new byte[lengthInBytes.length + bytes.length];
                System.arraycopy(lengthInBytes, 0, dataToSend, 0, lengthInBytes.length);
                System.arraycopy(bytes, 0, dataToSend, lengthInBytes.length, bytes.length);

                fileToSendTo.write(dataToSend);
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
                fileToReceiveFrom.read(lengthInBytes, 0, lengthInBytes.length);

                int length = ByteUtil.bytesToInt(lengthInBytes);
                byte[] receiveData = new byte[length];
                fileToReceiveFrom.read(receiveData, 0, length);
                result = receiveData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
