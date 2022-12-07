import java.io.*;
import java.net.*;

class UDPClient {
    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();

        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        boolean flag = true;

        System.out.print("Please enter your name: ");
                String name = inFromUser.readLine();
                String sendName = "N:" + name;
                sendData = sendName.getBytes();
                // create datagram with data-to-send, length, IP address, port
                DatagramPacket sendPacket =
                        new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                // send datagram to server
                clientSocket.send(sendPacket);



        while (flag) {
                System.out.print("Please enter your calculation: ");
                String expression = inFromUser.readLine();
                sendData = expression.getBytes();
                // create datagram with data-to-send, length, IP address, port
                sendPacket =
                        new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                // send datagram to server
                clientSocket.send(sendPacket);

        // GOES TO SERVER NOW
        
        // --------------------------------------------------------------

        // COMES BACK TO CLIENT HERE

                DatagramPacket receivePacket =
                        new DatagramPacket(receiveData, receiveData.length);
                //read datagram from server
                clientSocket.receive(receivePacket);

                String result = new String(receivePacket.getData());

                System.out.println("FROM SERVER: Result is " + result);

        // clientSocket.receive(receivePacket);
        // String IP = new String(receivePacket.getData());
        // System.out.println("The IP is: " + IP);

                System.out.print("Would you like to enter more calculations? Enter 'y' to continue: ");
                String response = inFromUser.readLine();
                if (!response.equals("y")) {
                        flag = false;
                        String closeRequest = "C:" + name;
                        sendData = closeRequest.getBytes();
                        sendPacket =
                        new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                // send datagram to server
                        clientSocket.send(sendPacket);
                        clientSocket.close();
                }

        }
    }
}