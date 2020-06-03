package namedpipetest;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

public final class Listener {
    private Server server;
    private String pipeName;
    private Responder responder;
    java.lang.Thread thread;
    java.lang.Thread thread1;

    public Listener(Server server, String pipeName, Responder responder) {
        this.server = server;
        this.pipeName = pipeName;
        this.responder = responder;
    }

    public void Listen() {
        String name = "\\\\.\\pipe\\" + pipeName;
        WinNT.HANDLE hNamedPipe = Kernel32.INSTANCE.CreateNamedPipe(name,
            WinBase.PIPE_ACCESS_DUPLEX,        // dwOpenMode
            WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT,    // dwPipeMode
            1,    // nMaxInstances,
            Byte.MAX_VALUE,    // nOutBufferSize,
            Byte.MAX_VALUE,    // nInBufferSize,
            0,    // nDefaultTimeOut,
            null);    // lpSecurityAttributes
        if (!WinBase.INVALID_HANDLE_VALUE.equals(hNamedPipe)) {
            Kernel32.INSTANCE.ConnectNamedPipe(hNamedPipe, null);
        }

        this.CreateRequester();

        thread = new Thread(() -> {
            responder.DoServices(new DataTransfer(hNamedPipe));
        });

        thread.setDaemon(true);
        thread.start();
    }

    public void Stop() {

    }

    private void CreateRequester() {
        String name = "\\\\.\\pipe\\requester" + pipeName;
        WinNT.HANDLE hNamedPipe = Kernel32.INSTANCE.CreateNamedPipe(name,
            WinBase.PIPE_ACCESS_DUPLEX,        // dwOpenMode
            WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT,    // dwPipeMode
            1,    // nMaxInstances,
            Byte.MAX_VALUE,    // nOutBufferSize,
            Byte.MAX_VALUE,    // nInBufferSize,
            0,    // nDefaultTimeOut,
            null);    // lpSecurityAttributes
        if (!WinBase.INVALID_HANDLE_VALUE.equals(hNamedPipe)) {
            Kernel32.INSTANCE.ConnectNamedPipe(hNamedPipe, null);
        }

        server.Connected(new DataTransfer(hNamedPipe));
    }
}
