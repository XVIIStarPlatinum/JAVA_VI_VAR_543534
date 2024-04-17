package client.ru.itmo.se;

import client.ru.itmo.se.utility.UserHandler;
import common.ru.itmo.se.exceptions.ConnectionErrorException;
import common.ru.itmo.se.exceptions.ValueRangeException;
import common.ru.itmo.se.interaction.Request;
import common.ru.itmo.se.interaction.Response;
import common.ru.itmo.se.utility.PrettyPrinter;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    private String host;
    private int port;
    private int reconnectionTimeout;
    private int reconnectionAttempts;
    private int maxReconnectionAttempts;
    private UserHandler userHandler;
    private SocketChannel socketChannel;
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;

    public Client(String host, int port, int reconnectionTimeout, int maxReconnectionAttempts, UserHandler userHandler) {
        this.host = host;
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeout;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
        this.userHandler = userHandler;
    }

    public void run() {
        try {
            boolean processingStatus = true;
            while(processingStatus) {
                try {
                    connectToServer();
                    processingStatus = processRequestToServer();
                } catch (ConnectionErrorException e) {
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        PrettyPrinter.printError("Connection attempts has exceeded the limit.");
                        break;
                    }
                    try {
                        Thread.sleep(reconnectionTimeout);
                    } catch (IllegalArgumentException timeout) {
                        PrettyPrinter.printError("Connection timeout value: '" + reconnectionTimeout + "' is beyond allowed limits.");
                        PrettyPrinter.println("A reconnection attempt will be made immediately.");
                    } catch (Exception timeoutException) {
                        PrettyPrinter.printError("An error occurred while for a connection.");
                        PrettyPrinter.println("A reconnection attempt will be made immediately.");
                    }
                }
                reconnectionAttempts++;
            }
            if(socketChannel != null) {
                socketChannel.close();
            }
            PrettyPrinter.println("Client session has been successfully terminated.");
        } catch (ValueRangeException e) {
            PrettyPrinter.printError("The client application cannot be launched.");
        } catch (IOException e) {
            PrettyPrinter.printError("An error occurred while attempting to connect to the server.");
        }
    }

    private void connectToServer() throws ConnectionErrorException, ValueRangeException {
        try {
            if(reconnectionAttempts >= 1) {
                PrettyPrinter.println("Reconnecting to the server...");
            }
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            PrettyPrinter.println("Connection to the server has been successfully established.");
            PrettyPrinter.println("Now waiting for data exchange permission...");
            serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());
            PrettyPrinter.println("Permission granted.");
        } catch (IllegalArgumentException e) {
            PrettyPrinter.printError("The server address is invalid.");
            throw new ValueRangeException("The server address is invalid.", new RuntimeException());
        } catch (IOException e) {
            PrettyPrinter.printError("An error occurred while attempting to connect to the server.");
            throw new ConnectionErrorException("An error occurred while attempting to connect to the server.", new RuntimeException());
        }
    }

    private boolean processRequestToServer() {
        Request requestToServer = null;
        Response responseFromServer = null;
        do {
            try {
                requestToServer = responseFromServer != null ? userHandler.handle(responseFromServer.getResponseCode()) : userHandler.handle(null);
                if(requestToServer.isEmpty()) {
                    continue;
                }
                serverWriter.writeObject(requestToServer);
                responseFromServer = (Response) serverReader.readObject();
                PrettyPrinter.print(responseFromServer.getResponseBody());
            } catch (InvalidClassException | NotSerializableException e) {
                PrettyPrinter.printError("An error occurred while trying to send data to the server.");
            } catch (ClassNotFoundException e) {
                PrettyPrinter.printError("An error occurred while trying to read data sent from the server.");
            } catch (IOException e) {
                PrettyPrinter.printError("A disconnection form the server occurred.");
                try {
                    reconnectionAttempts++;
                    connectToServer();
                } catch (ConnectionErrorException | ValueRangeException exception) {
                    if (requestToServer.getCommandObjArg().equals("exit")) {
                        PrettyPrinter.println("This command will not be registered on the server,");
                    } else {
                        PrettyPrinter.println("Please try later.");
                    }
                }
            }
        } while (!requestToServer.getCommandName().equals("exit"));
        return false;
    }
}
