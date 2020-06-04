package namedpipetest;

import java.io.IOException;

public final class Server {
    //    private ICustomImplEvents implEvents;
    private String id;
    Listener listener;
    //    CustomRequester requester;
    boolean isConnected = false;
    private Process currentProcess = null;

    Server(String id) {
//        this.implEvents = implEvents;
        this.id = id;
    }

    public void ExecuteService() {
//        if (this.requester != null)
//        {
//            this.requester.SendServicePb(servicePb);
//            this.requester.ReceiveResultPb(resultPb);
//        }
    }

    public void Connected(ClientDataTransfer dataTransfer) {
//        this.requester = new CustomRequester(dataTransfer);
        this.isConnected = true;
    }

    public void Start() throws IOException {
        if (this.listener == null) {
            this.listener = new Listener(this, this.id, new Responder());
            this.listener.Listen();
            while (!this.isConnected) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void Stop() {
        if (this.listener != null) {
            this.listener.Stop();
        }

        if (this.currentProcess != null) {
            this.currentProcess.destroyForcibly();
        }
    }
}
