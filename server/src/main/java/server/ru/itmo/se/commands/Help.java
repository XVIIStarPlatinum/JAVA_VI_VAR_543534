package server.ru.itmo.se.commands;

import common.ru.itmo.se.interaction.CommandType;
import lombok.ToString;
import common.ru.itmo.se.exceptions.InvalidArgumentCountException;
import server.ru.itmo.se.utility.CommandManager;
import server.ru.itmo.se.utility.ResponseAppender;

/**
 * This class implements the command Help. It outputs all commands and their specifications in a table format.
 * -- TOSTRING --
 * This method is a custom implementation of the toString() method in the Help class.
 */
@ToString
public class Help extends CommandImpl {
    /**
     * This field holds an instance of a CollectionManager which is responsible for operations with commands.
     */
    private CommandManager commandManager;
    /**
     * Constructs a Help.
     * @param commandManager the specified CommandManager.
     */
    public Help(CommandManager commandManager) {
        super("help", "", "Outputs a table of available commands", CommandType.WITHOUT_ARGS);
        this.commandManager = commandManager;
    }

    /**
     * This method is an implementation of the abstract apply() method for the Help command.
     *
     * @param commandStrArg the argument (unnecessary).
     * @param commandObjArg
     * @return true if the command was successfully executed, <p>false if the command encountered an error.
     */
    @Override
    public boolean apply(String commandStrArg, Object commandObjArg) {
        try {
            if (!commandStrArg.isEmpty() || commandObjArg != null) {
                throw new InvalidArgumentCountException("You don't need an argument here.", new RuntimeException());
            }
            ResponseAppender.appendTable("                          COMMAND NAME", "                                  COMMAND SPECIFICATION");
            for (CommandImpl command : commandManager.commandMap.values()) {

                ResponseAppender.appendTable(command.getName(), command.getSpec());
            }
            return true;
        } catch (InvalidArgumentCountException e) {
            ResponseAppender.appendln("Usage: '" + getName() + " " + getUsage() + "'");
        }
        return false;
    }
}
