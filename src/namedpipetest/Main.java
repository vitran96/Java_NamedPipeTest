package namedpipetest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        Thread listenerThread = new Thread(listener::Listen);
        listenerThread.setDaemon(true);
        listenerThread.setName("Listener thread");
        listenerThread.start();

        Client client = new Client(pipeName);
        client.Connect();

        client.SendThenReceiveMessage("Hello Server!");

        for (int i = 0; i < occurrences; i++) {
            client.SendThenReceiveMessage("Message number: " + i);
        }

        client.SendThenReceiveMessage("end");
    }
}
