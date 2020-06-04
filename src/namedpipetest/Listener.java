package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

import java.io.FileNotFoundException;
import java.util.function.Consumer;

public final class Listener {
//    private Server server;
    private String pipeName;
//    private Responder responder;
    java.lang.Thread thread;
    java.lang.Thread thread1;

    private Consumer<DataTransfer> callback;

    @Deprecated
    public Listener(Server server, String pipeName, Responder responder) {
//        this.server = server;
        this.pipeName = pipeName;
//        this.responder = responder;
    }

    public Listener(String pipeName, Consumer<DataTransfer> callback) {
        this.pipeName = "\\\\.\\pipe\\" + pipeName;
        this.callback = callback;
    }

    public void Listen() throws FileNotFoundException {
//        String name = "\\\\.\\pipe\\" + pipeName;
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

        System.out.println("Pipe listener started!");

        DataTransfer dataTransfer = new ListenerWinDataTransfer(hNamedPipe);
//        ClientDataTransfer dataTransfer = new ClientDataTransfer(this.pipeName);
        thread = new Thread(() -> callback.accept(dataTransfer));
        thread.setDaemon(true);
        thread.setName("Listener Thread");
        thread.start();
    }

    public void Stop() {
        // TODO: ???
    }

    @Deprecated
    private void CreateRequester() {
        String name = "\\\\.\\pipe\\requester" + pipeName;
        WinNT.HANDLE hNamedPipe = Kernel32.INSTANCE.CreateNamedPipe(
            name,
            WinBase.PIPE_ACCESS_DUPLEX,        // dwOpenMode
            WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT,    // dwPipeMode
            1,    // nMaxInstances,
            Byte.MAX_VALUE,    // nOutBufferSize,
            Byte.MAX_VALUE,    // nInBufferSize,g
            0,    // nDefaultTimeOut,
            null     // lpSecurityAttributes
        );

        if (!WinBase.INVALID_HANDLE_VALUE.equals(hNamedPipe)) {
            Kernel32.INSTANCE.ConnectNamedPipe(hNamedPipe, null);
        }

//        server.Connected(new DataTransfer(hNamedPipe));
    }
}
