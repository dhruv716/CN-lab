//Server.java:

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server is listening on port 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Start a new thread to handle each client
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Say Hello to the client
            out.println("Server: Hello! Type 'exit' to quit.");
            
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                System.out.println("Client says: " + clientMessage);
                if (clientMessage.equalsIgnoreCase("exit")) {
                    break;
                }

                out.println("Server: You said: " + clientMessage);
            }
            
            // File transfer
            out.println("Server: Ready to receive a file.");
            String fileName = in.readLine();
            receiveFile(in, fileName);

            clientSocket.close();
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveFile(BufferedReader in, String fileName) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Server: File received: " + fileName);
        }
    }
}



//Client.java:
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            // Say Hello to the server
            String serverMessage = in.readLine();
            System.out.println(serverMessage);
            
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in);
            String userInput;
            while (true) {
                userInput = userInput.readLine();
                out.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
                serverMessage = in.readLine();
                System.out.println(serverMessage);
            }
            
            // File transfer
            System.out.println("Server: " + in.readLine());
            String fileName = "sample.txt";
            out.println(fileName);
            sendFile(out, fileName);
            
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(PrintWriter out, String fileName) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        System.out.println("Client: File sent: " + fileName);
    }
}

