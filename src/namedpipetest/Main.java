package namedpipetest;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String pipeName = "pipetestonjava";
        if (args.length > 0)
            pipeName = args[0];

        int occurrences = 5;
        if (args.length > 1)
            occurrences = Integer.parseInt(args[1]);

        System.out.println("Named pipe: " + pipeName);
        System.out.println("Occurrences: " + occurrences);

        Responder responder = new Responder();

        Listener listener = new Listener(pipeName, responder::DoServices);
        Thread listenerThread = new Thread(() -> {
            try {
                listener.Listen();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.setName("Listener thread");
        listenerThread.start();

        Thread.sleep(5000);

        Client client = new Client(pipeName);
        client.Connect();

        client.SendThenReceiveMessage("Hello Server!");

        for (int i = 0; i < occurrences; i++) {
            client.SendThenReceiveMessage("Message number: " + i);
        }
    }
}
