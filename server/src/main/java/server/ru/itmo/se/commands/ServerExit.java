package server.ru.itmo.se.commands;

import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import common.ru.itmo.se.interaction.CommandType;
import server.ru.itmo.se.utility.ResponseAppender;

import lombok.ToString;
/**
 * This class implements the command server_exit. It gracefully terminates the CLI application on the server side.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the ServerExit class.
 */
@ToString
public class ServerExit extends CommandImpl {
    /**
     * Constructs a ServerExit.
     */
    public ServerExit() {
        super("server_exit", "", "Gracefully terminates the console application on the server side.", CommandType.WITHOUT_ARGS);
    }
    /**
     * This method is an implementation of the abstract apply() method for the server_exit command.
     * @param commandStrArg command's string argument.
     * @param commandObjArg command's object argument.
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if(!commandStrArg.isEmpty() || commandObjArg != null) {
                throw new InvalidArgumentCountException("You don't need an argument here.", new RuntimeException());
            }
            ResponseAppender.appendln("Server shutting down...");
            return true;
        } catch(InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + " " + getUsage() +  "'");
        }
        return false;
    }
}
