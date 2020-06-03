package namedpipetest;

public final class Responder {
    private boolean isStopped;

    public Responder() {
    }

    public void DoServices(DataTransfer dataTransfer) {
        try {
            while (!isStopped) {
                //
            }
        } catch (java.lang.Exception ex) {
            this.isStopped = true;
        }
    }
}
