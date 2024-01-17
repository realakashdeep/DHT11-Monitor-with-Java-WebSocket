package websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Client {
	public static void main(String[] args) throws IOException, InterruptedException {
		try (Scanner sc = new Scanner(System.in)) {
			System.out.println("Enter The Client IP");
			String ip = sc.nextLine();
			Socket clientSocket = new Socket(ip,2300);
	         
	        
	        // Start a thread for receiving messages
            Thread receiveThread = new Thread(() -> {
                try {
                    receiveFromServer(clientSocket);
                    Thread.sleep(1000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

 
		}
	}
	 private static void sendToServer(Socket socket, String message) throws IOException {
	        // Send a message to the server
	        OutputStream outputStream = socket.getOutputStream();
	        outputStream.write(message.getBytes());
	    }

	    private static void receiveFromServer(Socket socket) throws IOException {
	        // Receive a message from the server
	        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        while (true) {
	            // Receive a message from the server
	            String serverResponse = br.readLine();

//	            if (serverResponse == null) {
//	                // Connection closed by the server
//	            	//socket.close();
//	                break;
//	            }

	            System.out.println("Received from Server: " + serverResponse);
	        }
	    }
}
//"172.16.75.236"
//172.16.75.220
// Send messages in the main thread
//while (true) {
//  System.out.println("type 'exit' to stop");
//  String message = sc.nextLine();
//
//  if ("exit".equalsIgnoreCase(message)) {
//      // Close the connection and terminate the program
//      clientSocket.close();
//      break;
//  }
//
//  // Send the message to the server
//  //sendToServer(clientSocket, message);
//}localhost



