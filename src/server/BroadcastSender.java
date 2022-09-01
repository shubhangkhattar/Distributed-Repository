package server;

import java.net.*;
import java.util.*;
import java.util.Enumeration;

public class BroadcastSender {

    private static InetSocketAddress socketAddress = new InetSocketAddress(9999);
    private HashMap<String, String[]> addressMap;

    public static void sendBroadCaseMessage(String serverName, InetAddress inetAddress, Integer port, boolean enquire, String enquiryServerName,  HashMap<String, String[]> addressMap){
        try{
            DatagramSocket socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(socketAddress);
            socket.setBroadcast(true);

            String senderAddress = inetAddress.getHostAddress();
            senderAddress = senderAddress.replaceAll(",",".").replaceAll(" ","");
            String broadCastMessage;
            if(enquire){
                broadCastMessage = "SEND_ADDRESS:"+enquiryServerName;
            }
            else{
                broadCastMessage = serverName + ":" + senderAddress + "/" + port;
            }

            byte[] buffer = broadCastMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 9999);
            try{
                socket.send(packet);
            }
            catch (Exception e){
                System.out.println("error with sending a packet");
                e.printStackTrace();
            }
            List<InetAddress> inetAddresses = listAllBroadcastAddresses();
            for(InetAddress inet: inetAddresses){
                System.out.println("in broadcast sender: "+inet.getHostAddress()+" "+inet.getHostName()+" "+inet.getAddress()+" "+inet.getHostName());
                packet = new DatagramPacket(buffer, buffer.length, inet,9000);
                try{
                    socket.send(packet);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }
}
