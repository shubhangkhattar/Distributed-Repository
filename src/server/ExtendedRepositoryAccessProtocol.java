package server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static server.Utilities.sendln;

public class ExtendedRepositoryAccessProtocol extends RepositoryAccessProtocol{

    private static HashMap<String ,String[]> addressMap = new HashMap<>();

    public ExtendedRepositoryAccessProtocol(Repository repository, Socket socket, String connectionName, HashMap<String, String[]> addressMap) {
        super(repository, socket, connectionName);
        this.addressMap = addressMap;
    }

    @Override
    public void run() {
        printWriter.println("Hi");
        printWriter.println("Connection Name:"+connectionName+"\n");
        System.out.println("Address Map:"+addressMap);
        printWriter.flush();

        while(!stop){
            String data = scanner.nextLine();
            System.out.println(data);
            String[] clientCommand = data.split(" ");
            String operation = clientCommand[0].toUpperCase();
            System.out.println("READY FOR NEW INPUT");
            switch (operation){
                case "SET":{
                    if(clientCommand.length!=3){
                        sendln(printWriter, "Please provide input in proper format");
                        break;
                    }
                    String[] repositoryDataSplit = clientCommand[1].split("\\.");
                    if(repositoryDataSplit.length==2){
                        handleRemoteRequest(repositoryDataSplit, clientCommand, operation );
                    }
                    else{
                        repository.set(clientCommand[1], Integer.valueOf(clientCommand[2]));
                        sendln(printWriter,"OK");
                    }
                    break;
                }
                case "ADD":{
                    if(clientCommand.length!=3){
                        sendln(printWriter, "Please provide input in proper format");
                        break;
                    }
                    String[] repositoryDataSplit = clientCommand[1].split("\\.");
                    if(repositoryDataSplit.length==2){
                        handleRemoteRequest(repositoryDataSplit, clientCommand, operation );
                    }
                    else{
                        repository.add(clientCommand[1], Integer.valueOf(clientCommand[2]));
                        sendln(printWriter,"OK");
                    }
                    break;
                }
                case "GET":{
                    if(clientCommand.length!=2){
                        sendln(printWriter, "Please provide input in proper format");
                        break;
                    }
                    String[] repositoryDataSplit = clientCommand[1].split("\\.");
                    if(repositoryDataSplit.length==2){
                        handleRemoteRequest(repositoryDataSplit, clientCommand, operation );
                    }
                    else{
                        ArrayList<Integer> listValue =  repository.get(clientCommand[1]);
                        String s = "OK ";
                        if(listValue==null){
                            sendln(printWriter, "LIST IS EMPTY");
                        }
                        for(int val:listValue){
                            s += val+", ";
                        }
                        sendln(printWriter,s);
                    }
                    break;
                }
                case "SUM":{
                    if(clientCommand.length!=2){
                        sendln(printWriter, "Please provide input in proper format");
                        break;
                    }
                    String[] repositoryDataSplit = clientCommand[1].split("\\.");
                    if(repositoryDataSplit.length==2){
                        handleRemoteRequest(repositoryDataSplit, clientCommand, operation );
                    }
                    else{
                        ArrayList<Integer> listValue =  repository.get(clientCommand[1]);
                        String s = "OK ";
                        int sum =  listValue.stream().mapToInt(Integer::intValue).sum();
                        s += " " + sum;
                        sendln(printWriter,s);
                    }
                    break;
                }
                case "DSUM":{
                    if(clientCommand.length<=3){
                        sendln(printWriter, "Please provide input in proper format");
                        break;
                    }

                    int sum = 0;
                    for(int i=3;i<clientCommand.length;i++){
                        String[] repositoryDataSplit = new String[]{clientCommand[i], clientCommand[1]};
                        int ans = Integer.parseInt(handleRemoteRequest(repositoryDataSplit, clientCommand, operation));
                        if(ans==-404){
                            break;
                        }
                        sum += ans;
                    }
                    sendln(printWriter, String.valueOf(sum));
                    break;
                }
            }

        }
    }

    public String handleRemoteRequest(String[] repositoryDataSplit, String[] clientCommand, String action){
        String serverName = repositoryDataSplit[0];
        System.out.println(addressMap);
        String[] repositoryAddress = addressMap.get(serverName);
        if(repositoryAddress == null){
            sendln(printWriter, "ERR Non-existence or ambiguos repository:" + serverName);
            return "-404";
        }

        try{
            Socket socket = new Socket(repositoryAddress[0], Integer.parseInt(repositoryAddress[1]));
            Scanner inputStream = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            String commandString = buildCommandString(clientCommand, repositoryDataSplit, action);
            System.out.println("commandString "+commandString);
            sendln(writer, commandString);

            inputStream.nextLine();
            inputStream.nextLine();
            inputStream.nextLine();

            String response = inputStream.nextLine();

            if(action.equals("DSUM")){
                return response.split(" ")[2];
            }

            sendln(printWriter, response);
            return "0";


        }
        catch (Exception e){
            e.printStackTrace();
            return "0";
        }
    }

    public String buildCommandString(String[] clientCommand, String[] repositoryDataSplit, String action){
        switch (action){
            case "ADD": {
                return clientCommand[0] + " " + repositoryDataSplit[1] + " " + clientCommand[2];
            }
            case "SET":{
                return clientCommand[0] + " " + repositoryDataSplit[1] + " " + clientCommand[2];
            }
            case "DELETE":{
                return clientCommand[0] + " " + repositoryDataSplit[1];
            }
            case "LIST KEYS":{
                return action;
            }
            case "GET":{
                return action + " " + repositoryDataSplit[1];
            }
            case "DSUM":{
                return "SUM" + " " + repositoryDataSplit[1];
            }
            default: return "";
        }
    }
}
