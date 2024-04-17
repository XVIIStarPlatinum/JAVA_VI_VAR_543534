package common.ru.itmo.se.interaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Request implements Serializable {
    private String commandName;
    private String commandStrArg;
    private Serializable commandObjArg;

    public Request(String commandName, String commandStrArg) {
        this(commandName, commandStrArg, null);
    }

    public Request() {
        this("", "");
    }
    public boolean isEmpty() {
        return commandName.isEmpty() && commandStrArg.isEmpty() && commandObjArg == null;
    }

    @Override
    public String toString() {
        return "Request[" + commandName + " " + commandStrArg + " {" + commandObjArg + "}]";
    }
}
