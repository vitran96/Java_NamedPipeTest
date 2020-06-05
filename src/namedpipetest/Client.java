package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

import java.awt.image.Kernel;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;

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
            } catch (Exception e) {
                e.printStackTrace();
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
            WinNT.HANDLE hNamedPipe = Kernel32.INSTANCE.CreateFile(
                pipeName
                , WinNT.GENERIC_READ | WinNT.GENERIC_WRITE
                , 0
                , null
                , WinNT.OPEN_EXISTING
                , WinNT.FILE_FLAG_OVERLAPPED
                , null
            );

            if (hNamedPipe == INVALID_HANDLE_VALUE)
                throw new RuntimeException("Fail to connect named pipe file");

            dataTransfer = new WinDataTransfer(hNamedPipe);
        } else {
//            System.out.println("Pipe client for Linux");

            String pipeNameToSendTo = PipeConstant.CLIENT_2_SERVER_PREFIX + pipeName;
            String pipeNameToReceiveFrom = PipeConstant.SERVER_2_CLIENT_PREFIX + pipeName;

            dataTransfer = new LinuxDataTransfer(pipeNameToSendTo, pipeNameToReceiveFrom);
        }

        return dataTransfer;
    }
}
