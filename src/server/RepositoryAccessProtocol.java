package server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import static server.Utilities.sendln;

public class RepositoryAccessProtocol extends Thread {
    protected Repository repository;
    protected boolean stop = false;
    protected Socket socket;
    protected Scanner scanner;
    protected PrintWriter printWriter;
    protected String connectionName;



    public RepositoryAccessProtocol(Repository repository, Socket socket, String connectionName) {
        this.repository = repository;
        this.socket = socket;
        this.connectionName = connectionName;
        Socket s = new Socket();
        try{
            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream());
        }
        catch (Exception e){
            System.out.println("There are some issues with initialization of reader and writer");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        printWriter.write("Hello\nYour unique ID is:"+connectionName+"\n");
        printWriter.flush();
        while(!stop){
            String data = scanner.nextLine();
            String[] splittedData = data.split(" ");
            System.out.println(splittedData[0]);
            switch (splittedData[0].toUpperCase()) {
                case "SET":
                    repository.set(splittedData[1], Integer.valueOf(splittedData[2]));
                    sendln(printWriter,"OK");
                    break;
                case "ADD":
                    repository.add(splittedData[1], Integer.valueOf(splittedData[2]));
                    sendln(printWriter,"OK");
                    break;
                case "DELETE":
                    repository.delete(splittedData[1]);
                    sendln(printWriter,"OK");
                    break;
                case "GET":
                    ArrayList<Integer> listValue =  repository.get(splittedData[1]);
                    String s = "OK ";
                    for(int val:listValue){
                        s += val+", ";
                    }
                    sendln(printWriter,s);
                    break;
                case "SUM":
                    int sum = repository.sum(splittedData[1]);
                    sendln(printWriter,"OK "+sum);
                    break;
                default:
                    sendln(printWriter,"ERR Sorry did not understand. Say BYE if you wish to exit.");
                    break;
            }
        }
    }
}
