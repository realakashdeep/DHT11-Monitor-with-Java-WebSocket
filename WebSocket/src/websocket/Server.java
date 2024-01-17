package websocket;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	
    private static SerialPort arduinoPort; // Declare the SerialPort variable

    public static void main(String[] args) throws IOException {
        System.out.println("Server Started");
        
        SerialPort[] list = SerialPort.getCommPorts();
        for (SerialPort port : list) {
            System.out.println("Port Name: " + port.getSystemPortName());
            System.out.println("Descriptive Port Name: " + port.getDescriptivePortName());
            System.out.println("Baud Rate: " + port.getBaudRate());
            System.out.println("----------");
        }
        for (SerialPort port : list) {
            if (port.getSystemPortName().equals("COM5")) {
                if (port.openPort()) {
                	arduinoPort = port;
                    System.out.println("COM5 port opened successfully."+arduinoPort);
                    // Perform further operations with the opened port if needed
                } else {
                    System.err.println("Failed to open COM5 port.");
                }
                break; // Exit the loop once COM5 is found and opened
            }
        }

        try (ServerSocket welcomeSocket = new ServerSocket(2300)) {

            // Connect to the Arduino over serial communication
            
            while (true) {
                // Accept a client connection
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Welcome Client");

                // Create a new thread to handle the client and pass the Arduino serial port
                Thread clientThread = new Thread(() -> handleClient(connectionSocket));
                clientThread.start();
            }
        }
    }

    private static void handleClient(Socket socket) {
        try {
            // Send a welcome message to the client
            sendToClient(socket, "Welcome to the server!");
            

            // Continuously read data from Arduino and send it to the client
            while (true) {
                String arduinoData = readFromArduino();
                sendToClient(socket, arduinoData);
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the socket when the client disconnects
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendToClient(Socket socket, String message) throws IOException {
        // Send a message to the client
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter outToClient = new PrintWriter(outputStream, true);
        outToClient.println(message);
    }

    private static String readFromArduino() throws IOException {
        final StringBuilder receivedData = new StringBuilder();
        
//        System.out.println("ReadFromArduino");
        arduinoPort.addDataListener(new SerialPortDataListener() {
            @Override
            public void serialEvent(SerialPortEvent event) {
                int size = event.getSerialPort().bytesAvailable();
                byte[] buffer = new byte[size];
                arduinoPort.readBytes(buffer, size);
                //System.out.println(buffer);

                // Append the received data to the StringBuilder
                receivedData.append(new String(buffer));
            }

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
        });

        // Sleep for a short time to allow data to be received (adjust as needed)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Remove the listener to avoid resource leaks
        arduinoPort.removeDataListener();

        // Return the accumulated received data
        return receivedData.toString();
    }


}
