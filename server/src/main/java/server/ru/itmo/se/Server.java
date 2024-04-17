package server.ru.itmo.se;

import common.ru.itmo.se.exceptions.ClosingSocketException;
import common.ru.itmo.se.exceptions.ConnectionErrorException;
import common.ru.itmo.se.exceptions.OpeningServerSocketException;
import common.ru.itmo.se.interaction.Request;
import common.ru.itmo.se.interaction.Response;
import common.ru.itmo.se.interaction.ResponseCode;
import common.ru.itmo.se.utility.PrettyPrinter;
import server.ru.itmo.se.utility.RequestHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.*;

public class Server {
    private int port;
    private int soTimeout;
    private ServerSocket serverSocket;
    private RequestHandler requestHandler;

    public Server(int port, int soTimeout, RequestHandler requestHandler) {
        this.port = port;
        this.soTimeout = soTimeout;
        this.requestHandler = requestHandler;
    }

    public void run() {
        try {
            openServerSocket();
            boolean processingStatus = true;
            while(processingStatus) {
                try (Socket clientSocket = connectToClient()) {
                    processingStatus = processClientRequest(clientSocket);
                } catch (ConnectionErrorException | SocketTimeoutException e) {
                    break;
                } catch (IOException e) {
                    PrettyPrinter.printError("An error occurred while trying to cut the connection to the client.");
                    App.logger.log(Level.SEVERE, "An error occurred while trying to cut the connection to the client.");
                }
            }
            stop();
        } catch (OpeningServerSocketException e) {
            PrettyPrinter.printError("The server cannot be launched.");
            App.logger.log(Level.SEVERE, "FATAL: The server cannot be launched.");
        }
    }

    /**
     *
     */
    public void stop() {
        try {
            App.logger.log(Level.INFO, "Terminating the server session.");
            if(serverSocket == null) {
                throw new ClosingSocketException("Cannot terminate a server that hasn't been launched.", new RuntimeException());
            }
            serverSocket.close();
            PrettyPrinter.println("The server session has been terminated.");
            App.logger.log(Level.INFO, "The server session has been terminated.");
        } catch (ClosingSocketException e) {
            PrettyPrinter.printError("Cannot terminate a server that hasn't been launched.");
            App.logger.log(Level.SEVERE, "Cannot terminate a server that hasn't been launched.");
        } catch (IOException e) {
            PrettyPrinter.printError("An error occurred while trying to close the server.");
            App.logger.log(Level.SEVERE, "FATAL: An error occurred while trying to close the server.");
        }
    }

    private void openServerSocket() {
        try {
            App.logger.log(Level.INFO, "Starting the server...");
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(soTimeout);
            App.logger.log(Level.INFO, "The server has been successfully launched.");
        } catch (IllegalArgumentException e) {
            PrettyPrinter.printError("The port '" + port + "' is outside the allowed range.");
            App.logger.log(Level.SEVERE, "FATAL: The port '" + port + "' is outside the allowed range.");
            throw new OpeningServerSocketException("The port '" + port + "' is outside the allowed range.", new RuntimeException());
        } catch (IOException e) {
            PrettyPrinter.printError("An error occurred while trying to use the port '" + port + "'.");
            App.logger.log(Level.SEVERE, "FATAL: An error occurred while trying to use the port '" + port + "'.");
            throw new OpeningServerSocketException("The port '" + port + "' is outside the allowed range.", new RuntimeException());
        }
    }

    private Socket connectToClient() throws ConnectionErrorException, SocketTimeoutException {
        try {
            PrettyPrinter.println("Listening on port '" + port + "'...");
            App.logger.log(Level.INFO, "Listening on port '" + port + "'...");
            Socket clientSocket = serverSocket.accept();
            PrettyPrinter.println("Connection has been successfully established.");
            App.logger.log(Level.INFO, "Connection has been successfully established.");
            return clientSocket;
        } catch (SocketTimeoutException e) {
            PrettyPrinter.printError("Timeout occurred while trying to connect to the server.");
            App.logger.log(Level.SEVERE, "Timeout occurred while trying to connect to the server.");
            throw new SocketTimeoutException("Timeout occurred while trying to connect to the server.");
        } catch (IOException e) {
            PrettyPrinter.printError("An error occurred while trying to connect to the server.");
            App.logger.log(Level.SEVERE, "FATAL: An error occurred while trying to connect to the server.");
            throw new ConnectionErrorException("An error occurred while trying to connect to the server.", new RuntimeException());
        }
    }
    /**
     *
     * @param clientSocket
     * @return
     */
    private boolean processClientRequest(Socket clientSocket) {
        Request requestFromUser = null;
        Response responseToUser;
        try (ObjectInputStream clientReader = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.getOutputStream())) {
                 do {
                     requestFromUser = (Request) clientReader.readObject();
                     responseToUser = requestHandler.handle(requestFromUser);
                     App.logger.log(Level.INFO, "Request '" + requestFromUser.getCommandName() + "' has been successfully received.");
                     clientWriter.writeObject(responseToUser);
                     clientWriter.flush();
                 } while (responseToUser.getResponseCode() != ResponseCode.SERVER_EXIT);
            return false;
        } catch (ClassNotFoundException e) {
            PrettyPrinter.printError("An error occurred while trying to read data from the user.");
            App.logger.log(Level.SEVERE, "An error occurred while trying to read data from the user.");
        } catch (InvalidClassException | NotSerializableException e) {
            PrettyPrinter.printError("An error occurred while trying to send data to the user.");
            App.logger.log(Level.SEVERE, "An error occurred while trying to send data to the user.");
        } catch (IOException e) {
            if(requestFromUser == null) {
                PrettyPrinter.printError("An unexpected disconnection from the client occurred.");
                App.logger.log(Level.WARNING, "An unexpected disconnection from the client occurred.");
            } else {
                PrettyPrinter.println("The client has successfully disconnected from the server.");
                App.logger.log(Level.INFO, "The client has successfully disconnected from the server.");
            }
        }
        return true;
    }
}

