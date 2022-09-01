package server;

import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to single repository access dictionary");
        System.out.println("Server is starting right now");
        SingleRepositoryServer singleRepositoryServer = new SingleRepositoryServer("server1", 5000);
        singleRepositoryServer.start();
        Thread.sleep(1000);
        System.out.println("Type and press exit to stop the server");
        Scanner scanner = new Scanner(System.in);
        while(true){
            String input = scanner.nextLine();
            if(input.equals("exit")){
                System.out.println("Server is turning off");

                return;
            }
        }
    }
}
