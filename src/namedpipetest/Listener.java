package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

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
        } catch (Exception e) {
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

    private DataTransfer MakeDataTransfer() throws IOException, InterruptedException {
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

            dataTransfer = new WinDataTransfer(hNamedPipe);
        } else {
            System.out.println("Linux FIFO.");
            String pipeNameToSendTo = PipeConstant.SERVER_2_CLIENT_PREFIX + pipeName;
            String pipeNameToReceiveFrom = PipeConstant.CLIENT_2_SERVER_PREFIX + pipeName;

            new ProcessBuilder("mkfifo", pipeNameToSendTo).start().waitFor();
            new ProcessBuilder("mkfifo", pipeNameToReceiveFrom).start().waitFor();

            dataTransfer = new LinuxDataTransfer(pipeNameToSendTo, pipeNameToReceiveFrom);
        }

        System.out.println("Pipe listener started!");

        return dataTransfer;
    }
}
