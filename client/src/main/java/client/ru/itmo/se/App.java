package client.ru.itmo.se;

import client.ru.itmo.se.utility.UserHandler;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import common.ru.itmo.se.exceptions.ValueRangeException;
import common.ru.itmo.se.utility.PrettyPrinter;

import java.io.File;
import java.util.Scanner;

public class App {
    /**
     *
     */
    public static final String CS1 = "$ ";
    /**
     *
     */
    public static final String CS2 = "> ";
    /**
     *
     */
    private static final int RECONNECTION_TIMEOUT = 5 * 1000;
    /**
     *
     */
    private static final int MAX_RECONNECTION_ATTEMPTS = 5;
    /**
     *
     */
    private static String host;
    /**
     *
     */
    private static int port;

    /**
     *
     * @param hostAndPort
     * @return
     */
    private static boolean initConnection(String[] hostAndPort) {
        try {
            if(hostAndPort.length != 2) {
                throw new InvalidArgumentCountException("Incorrect jar usage.", new RuntimeException());
            }
            host = hostAndPort[0];
            port = Integer.parseInt(hostAndPort[1]);
            if(port < 0) {
                throw new ValueRangeException("Port value cannot be negative.", new RuntimeException());
            }
            return true;
        } catch (InvalidArgumentCountException e) {
            String jarName = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
            PrettyPrinter.println("Usage: 'java -jar " + jarName + " <host> <port>'");
        } catch (NumberFormatException e) {
            PrettyPrinter.printError("Port value must be a number.");
        } catch (ValueRangeException e) {
            PrettyPrinter.printError("Port value cannot be negative.");
        }
        return false;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        if(!initConnection(args)) {
            return;
        }
        Scanner userScanner = new Scanner(System.in);
        UserHandler userHandler = new UserHandler(userScanner);
        Client client = new Client(host, port, RECONNECTION_TIMEOUT, MAX_RECONNECTION_ATTEMPTS, userHandler);
        client.run();
        userScanner.close();
    }
}