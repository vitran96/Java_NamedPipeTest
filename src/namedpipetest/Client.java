package namedpipetest;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class Client {

    private String pipeName;
    private GenericDataTransfer dataTransfer;

    public Client(String pipeName) {
        this.pipeName = PipeUtil.MakePipeName(pipeName);
    }

    public void Connect() throws FileNotFoundException {
        this.dataTransfer = new GenericDataTransfer(pipeName);
    }

    public String SendThenReceiveMessage(String sendMessage) throws IOException {
        byte[] dataToSend = ByteUtil.String2Bytes(sendMessage);

        dataTransfer.SendData(dataToSend);

        byte[] receiveData = dataTransfer.ReceiveData();
        String receiveMessage = ByteUtil.Bytes2String(receiveData);

        System.out.println("Message from Server: " + receiveMessage);

        return receiveMessage;
    }
}
