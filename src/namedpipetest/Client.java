package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class Client {

    private String pipeName;
    private DataTransfer dataTransfer;

    public Client(String pipeName) {
        this.pipeName = "\\\\.\\pipe\\" + pipeName;
    }

    public void Connect() throws FileNotFoundException {
//        String name = "\\\\.\\pipe\\requester" + pipeName;
//        String name = "\\\\.\\pipe\\" + pipeName;
        WinNT.HANDLE hNamedPipe = Kernel32.INSTANCE.CreateNamedPipe(
            pipeName,
            WinBase.PIPE_ACCESS_DUPLEX,        // dwOpenMode
            WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT,    // dwPipeMode
            1,    // nMaxInstances,
            Byte.MAX_VALUE,    // nOutBufferSize,
            Byte.MAX_VALUE,    // nInBufferSize,g
            0,    // nDefaultTimeOut,
            null     // lpSecurityAttributes
        );

//        if (!WinBase.INVALID_HANDLE_VALUE.equals(hNamedPipe)) {
//            Kernel32.INSTANCE.ConnectNamedPipe(hNamedPipe, null);
//        }

//        this.dataTransfer = new DataTransfer(hNamedPipe);
        this.dataTransfer = new DataTransfer(pipeName);
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
