package namedpipetest;

import java.io.IOException;

public interface DataTransfer {
    void SendData(byte[] bytes);
    byte[] ReceiveData();
}
