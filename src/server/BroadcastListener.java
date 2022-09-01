package server;

import javax.xml.crypto.Data;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class BroadcastListener extends Thread {
    private HashMap<String, String[]> addressMap;
    private boolean stop;
    private DatagramSocket socket;
    private static InetSocketAddress socketAddress = new InetSocketAddress(9000);
    private String connetionName;
    private InetAddress tcpInetAddress;
    private Integer myPort;

    public BroadcastListener(HashMap<String, String[]> addressMap, String connetionName, InetAddress tcpInetAddress, Integer myPort) throws SocketException {
        this.addressMap = addressMap;
        this.connetionName = connetionName;
        this.tcpInetAddress = tcpInetAddress;
        this.myPort = myPort;
        stop = false;
        socket = new DatagramSocket(null);
        socket.setBroadcast(true);
        socket.setReuseAddress(true);
        socket.bind(socketAddress);
    }


    @Override
    public void run() {
        while(!stop){
            try{
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String dataReceived = new String(packet.getData(),0, packet.getLength());
                System.out.println("we are here:"+dataReceived+" "+addressMap);
                dataReceived = dataReceived.replaceAll("\0","");
                String[] splitData =dataReceived.split(":");
                if(splitData[0].equals("SEND_ADDRESS") && splitData[1].equals(connetionName)){
                    String myAddress = tcpInetAddress.getHostAddress();
                    myAddress = myAddress.replaceAll(",", ".").replaceAll(" ", "");
                    String broadCastMessage = connetionName + ":" + myAddress + "/" + myPort;
                    socket.send(new DatagramPacket(broadCastMessage.getBytes(StandardCharsets.UTF_8), broadCastMessage.getBytes(StandardCharsets.UTF_8).length, packet.getAddress(), packet.getPort()));
                }
                else if(!addressMap.containsKey(splitData[0])){
                    String[] address = splitData[1].split("/");
                    System.out.println(dataReceived);
                    addressMap.put(splitData[0], address);
                    String enquiryName = connetionName + ":" + tcpInetAddress.getHostAddress() + "/" + myPort;
                    BroadcastSender.sendBroadCaseMessage(connetionName, InetAddress.getLocalHost(), myPort, false, enquiryName, addressMap);
                    if(splitData[0]!=connetionName){

                    }

                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
