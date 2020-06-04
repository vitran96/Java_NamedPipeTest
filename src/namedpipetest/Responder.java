package namedpipetest;

public final class Responder {
    private boolean isStopped;

    public Responder() {
    }

    public void DoServices(DataTransfer dataTransfer) {
        try {
            while (!isStopped) {

                System.out.println("Receive data from client");
                byte[] receiveData = dataTransfer.ReceiveData();
                if (receiveData.length == 0)
                    continue;

                String message = ByteUtil.Bytes2String(receiveData);

                System.out.println("Message from client: " + message);

                byte[] replyData = new byte[0];

                if (message.equals("Hello Server!")) {
                    replyData = ByteUtil.String2Bytes("Hi Client!");
                } else if (message.equals("end")) {
                    isStopped = true;
                    replyData = ByteUtil.String2Bytes("Bye!");
                } else if (!message.isEmpty()) {
                    replyData = ByteUtil.String2Bytes("Did you say this: " + message);
                }

                System.out.println("Sending data to client");
                dataTransfer.SendData(replyData);
            }
        } catch (java.lang.Exception ex) {
            this.isStopped = true;
        }
    }
}
