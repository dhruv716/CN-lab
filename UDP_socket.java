//Server.java:

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(9876);
            System.out.println("Server is running on port 9876...");

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received data from client: " + receivedData);

                if (receivedData.equals("exit")) {
                    break;
                }

                String fileName = "received_" + receivedData;
                receiveFile(socket, clientAddress, clientPort, fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private static void receiveFile(DatagramSocket socket, InetAddress clientAddress, int clientPort, String fileName)
            throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        byte[] buffer = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);

            if (new String(receivePacket.getData(), 0, receivePacket.getLength()).equals("end")) {
                fileOutputStream.close();
                System.out.println("Received file: " + fileName);
                break;
            }

            fileOutputStream.write(receivePacket.getData(), 0, receivePacket.getLength());
        }

        byte[] ackData = "ACK".getBytes();
        DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, clientAddress, clientPort);
        socket.send(ackPacket);
    }
}


//Client.java:

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");

            sendFile(socket, serverAddress, 9876, "sample.txt");
            sendFile(socket, serverAddress, 9876, "sample.wav");
            sendFile(socket, serverAddress, 9876, "sample.mp4");
            sendExitSignal(socket, serverAddress, 9876);

            socket.receive(new DatagramPacket(new byte[1024], 1024));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private static void sendFile(DatagramSocket socket, InetAddress serverAddress, int serverPort, String fileName)
            throws IOException {
        byte[] fileData = new byte[1024];
        FileInputStream fileInputStream = new FileInputStream(fileName);
        int bytesRead;

        DatagramPacket fileNamePacket = new DatagramPacket(fileName.getBytes(), fileName.length(), serverAddress, serverPort);
        socket.send(fileNamePacket);

        while ((bytesRead = fileInputStream.read(fileData)) != -1) {
            DatagramPacket dataPacket = new DatagramPacket(fileData, bytesRead, serverAddress, serverPort);
            socket.send(dataPacket);
        }

        byte[] endSignal = "end".getBytes();
        DatagramPacket endPacket = new DatagramPacket(endSignal, endSignal.length, serverAddress, serverPort);
        socket.send(endPacket);
        fileInputStream.close();

        byte[] ackData = new byte[3];
        DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length);
        socket.receive(ackPacket);
    }

    private static void sendExitSignal(DatagramSocket socket, InetAddress serverAddress, int serverPort) throws IOException {
        DatagramPacket exitPacket = new DatagramPacket("exit".getBytes(), 4, serverAddress, serverPort);
        socket.send(exitPacket);
    }
}
