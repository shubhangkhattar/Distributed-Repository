package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class DistributeRepositoryServer extends SingleRepositoryServer{
    private static HashMap<String, String[]> addressMap = new HashMap<>();


    public DistributeRepositoryServer(String socketName, int port) throws IOException, UnknownHostException {
        super(socketName, port);
        BroadcastSender.sendBroadCaseMessage(socketName, InetAddress.getLocalHost(), port, false, null, addressMap);
        BroadcastListener broadcastListener = new BroadcastListener(addressMap, socketName, serverSocket.getInetAddress(), port);
        broadcastListener.start();
    }

    @Override
    public void run() {
        System.out.println("Distributed repository server "+ socketName +" started successfully!");
        System.out.println("Address map for server: "+ addressMap);
        while (!stop){
            try {
                Socket s = serverSocket.accept();
                String connectionId = String.valueOf((int)Math.random() * 100) + s.getPort();
                ExtendedRepositoryAccessProtocol repositoryAccessProtocol = new ExtendedRepositoryAccessProtocol(repository, s, connectionId, addressMap);
                threadArrayList.add(repositoryAccessProtocol);
                repositoryAccessProtocol.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
