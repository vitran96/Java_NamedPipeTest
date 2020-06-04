package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

public final class Listener {
    private String pipeName;

    private Consumer<DataTransfer> callback;

    public Listener(String pipeName, Consumer<DataTransfer> callback) {
        this.pipeName = PipeUtil.MakePipeName(pipeName);
        this.callback = callback;
    }

    public void Listen() {

        DataTransfer dataTransfer;
        try {
            dataTransfer = MakeDataTransfer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Thread thread = new Thread(() -> callback.accept(dataTransfer));
        thread.setDaemon(true);
        thread.setName("Listener Thread");
        thread.start();
    }

    public void Stop() {
        // TODO: ???
    }

    private DataTransfer MakeDataTransfer() throws IOException {
        DataTransfer dataTransfer;
        if (OsUtil.GetGeneralOsName().equals(OsUtil.WINDOWS)) {
            System.out.println("Windows named pipe.");
            WinNT.HANDLE hNamedPipe = Kernel32.INSTANCE.CreateNamedPipe(
                pipeName,
                WinBase.PIPE_ACCESS_DUPLEX,                   // dwOpenMode
                WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT,    // dwPipeMode
                1,                                        // nMaxInstances,
                Byte.MAX_VALUE,                               // nOutBufferSize,
                Byte.MAX_VALUE,                               // nInBufferSize,
                0,                                        // nDefaultTimeOut,
                null                          // lpSecurityAttributes
            );

            if (!WinBase.INVALID_HANDLE_VALUE.equals(hNamedPipe)) {
                Kernel32.INSTANCE.ConnectNamedPipe(hNamedPipe, null);
            }

            dataTransfer = new ListenerWinDataTransfer(hNamedPipe);
        } else {
            System.out.println("Linux FIFO.");
            ProcessBuilder processBuilder = new ProcessBuilder("mkfifo", pipeName);
            processBuilder.start();

            dataTransfer = new GenericDataTransfer(pipeName);
        }

        System.out.println("Pipe listener started!");

        return dataTransfer;
    }
}
