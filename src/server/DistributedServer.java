package server;

public class DistributedServer {

    public static void main(String[] args) throws Exception {
        System.out.println("we are in distributed server right now");
        DistributeRepositoryServer distributeRepositoryServer = new DistributeRepositoryServer(args[0], Integer.parseInt(args[1]));
        distributeRepositoryServer.start();
    }
}
