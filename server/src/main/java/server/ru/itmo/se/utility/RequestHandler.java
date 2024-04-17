package server.ru.itmo.se.utility;

import common.ru.itmo.se.interaction.Request;
import common.ru.itmo.se.interaction.Response;
import common.ru.itmo.se.interaction.ResponseCode;

public class RequestHandler {

    private CommandManager commandManager;

    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Response handle(Request request) {
        commandManager.addToHistory(request.getCommandName());
        ResponseCode responseCode = executeCommand(request.getCommandName(), request.getCommandStrArg(), request.getCommandObjArg());
        return new Response(responseCode, ResponseAppender.getAndClear());
    }

    private ResponseCode executeCommand(String commandName, String commandStrArg, Object commandObjArg) {
        if(commandManager.commandMap.containsKey(commandName)) {
            if(commandName.equals("server_exit")){
                return (commandManager.commandMap.get("server_exit").apply(commandStrArg, commandObjArg)) ? ResponseCode.SERVER_EXIT : ResponseCode.ERROR;
            } else {
                return (commandManager.commandMap.get(commandName).apply(commandStrArg, commandObjArg)) ? ResponseCode.OK : ResponseCode.ERROR;
            }
        } else if(commandName.isEmpty()) {
            return ResponseCode.ERROR;
        } else {
            CommandManager.noSuchCommand(commandName);
        }
        return ResponseCode.ERROR;
    }
}
