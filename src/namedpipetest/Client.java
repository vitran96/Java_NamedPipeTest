package namedpipetest;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class Client {

    private String pipeName;
    private DataTransfer dataTransfer;

    public Client(String pipeName) {
        this.pipeName = PipeUtil.MakePipeName(pipeName);
    }

    public void Connect() {
        boolean isConnected = false;
        while (!isConnected) {
            try {
                this.dataTransfer = MakeDataTransfer();
                isConnected = true;
            } catch (FileNotFoundException ignored) {
            }
        }
        System.out.println("Pipe client started");
    }

    public String SendThenReceiveMessage(String sendMessage) {
        byte[] dataToSend = ByteUtil.String2Bytes(sendMessage);

        System.out.println("Sending data to server");
        dataTransfer.SendData(dataToSend);

        System.out.println("Receive data from server");
        byte[] receiveData = dataTransfer.ReceiveData();
        String receiveMessage = ByteUtil.Bytes2String(receiveData);

        System.out.println("Message from Server: " + receiveMessage);

        return receiveMessage;
    }

    public DataTransfer MakeDataTransfer() throws FileNotFoundException {
        DataTransfer dataTransfer;
        if (OsUtil.GetGeneralOsName().equals(OsUtil.WINDOWS)) {
//            System.out.println("Pipe client for Windows");
            dataTransfer = new PipeFileDataTransfer(pipeName);
        } else {
//            System.out.println("Pipe client for Linux");

            String pipeNameToSendTo = PipeConstant.CLIENT_2_SERVER_PREFIX + pipeName;
            String pipeNameToReceiveFrom = PipeConstant.SERVER_2_CLIENT_PREFIX + pipeName;

            dataTransfer = new LinuxDataTransfer(pipeNameToSendTo, pipeNameToReceiveFrom);
        }

        return dataTransfer;
    }
}
