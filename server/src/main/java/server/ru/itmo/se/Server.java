package server.ru.itmo.se;

import common.ru.itmo.se.exceptions.ClosingSocketException;
import common.ru.itmo.se.exceptions.ConnectionErrorException;
import common.ru.itmo.se.exceptions.OpeningServerSocketException;
import common.ru.itmo.se.interaction.Request;
import common.ru.itmo.se.interaction.Response;
import common.ru.itmo.se.interaction.ResponseCode;
import common.ru.itmo.se.utility.PrettyPrinter;
import server.ru.itmo.se.utility.CollectionManager;
import server.ru.itmo.se.utility.RequestHandler;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.*;

public class Server {
    private int port;
    private int soTimeout;
    private ServerSocket serverSocket;
    private RequestHandler requestHandler;
    private Selector serverSelector;
    private Request request;
    private Response response;
    public Server(int port, int soTimeout, RequestHandler requestHandler) {
        this.port = port;
        this.soTimeout = soTimeout;
        this.requestHandler = requestHandler;
    }

    public void run() {
        Request requestFromUser = null;
        Response responseToUser;
        try {
            openServerSocket();
            while (true) {
                serverSelector.select();
                Iterator<SelectionKey> selectedKeys = serverSelector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    try {
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                                SocketChannel clientChannel = serverSocketChannel.accept();
                                clientChannel.configureBlocking(false);
                                clientChannel.register(serverSelector, SelectionKey.OP_READ);
                            }
                            if (key.isReadable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                clientChannel.configureBlocking(false);
                                ByteBuffer clientData = ByteBuffer.allocate(4096);
                                try (ObjectInputStream clientDataIn = new ObjectInputStream(new ByteArrayInputStream(clientData.array()))) {
                                    request = (Request) clientDataIn.readObject();
                                } catch (StreamCorruptedException e) {
                                    key.cancel();
                                }
                                if (request != null) {
                                    response = requestHandler.handle(request);
                                }
                            }
                            if (key.isWritable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                clientChannel.configureBlocking(false);
                                try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                     ObjectOutputStream clientDataOut = new ObjectOutputStream(bytes)) {
                                    clientDataOut.writeObject(response);

                                    ByteBuffer clientData = ByteBuffer.wrap(bytes.toByteArray());
                                    ByteBuffer dataLen = ByteBuffer.allocate(32).putInt(clientData.limit());
                                    dataLen.flip();

                                    clientChannel.write(dataLen);
                                    App.logger.log(Level.INFO, "Response length: " + dataLen.limit());
                                    clientChannel.write(clientData);
                                    App.logger.log(Level.INFO, "Response has been successfully sent to the client.");
                                    clientData.clear();
                                }
                                clientChannel.register(serverSelector, SelectionKey.OP_READ);
                            }
                        }
                    } catch (SocketException | CancelledKeyException e) {
                        App.logger.log(Level.WARNING, "The client '" + key.channel().toString() + "' has disconnected.");
                        key.cancel();
                    }
                    selectedKeys.remove();
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        } catch (OpeningServerSocketException e) {
            PrettyPrinter.printError("The server cannot be launched.");
            App.logger.log(Level.SEVERE, "FATAL: The server cannot be launched.");
        } catch (IOException e) {
            PrettyPrinter.printError("An I/O error occurred.");
            App.logger.log(Level.SEVERE, "An I/O error occurred.");
        } catch (ClassNotFoundException e) {
            PrettyPrinter.printError("Non-matching classes.");
            App.logger.log(Level.SEVERE, "Non-matching classes.");
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
            serverSelector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            App.logger.log(Level.INFO, "The server socket channel is ready for operations.");
            serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
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
}

