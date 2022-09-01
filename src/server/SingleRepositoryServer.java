package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class SingleRepositoryServer extends Thread{
    protected ServerSocket serverSocket;
    protected boolean stop;
    protected ArrayList<RepositoryAccessProtocol> threadArrayList;
    protected Repository repository;
    protected String socketName;

    public SingleRepositoryServer(String socketName, int port) {
        try{
            this.serverSocket = new ServerSocket(port);
            stop = false;
            threadArrayList = new ArrayList<>();
            repository = new Repository();
            this.socketName = socketName;
        }
        catch (Exception e){
            System.out.println("Issue with server Socket");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("New Single Repository Access just started");
        while(!stop){
            try{
                Socket socket = serverSocket.accept();
                UUID uuid = UUID.randomUUID();
                String uuId = uuid.toString();
                RepositoryAccessProtocol repositoryAccessProtocol = new RepositoryAccessProtocol(repository, socket, uuId);
                threadArrayList.add(repositoryAccessProtocol);
                repositoryAccessProtocol.start();

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
