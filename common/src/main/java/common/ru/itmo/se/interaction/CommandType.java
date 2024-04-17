package common.ru.itmo.se.interaction;

import java.io.Serializable;

import lombok.Getter;

@Getter
public enum CommandType implements Serializable {
    WITHOUT_ARGS(0, false),
    WITH_FORM(0, true),
    WITH_ARGS(1, false),
    WITH_ARGS_FORM(1, true);

    private final int argumentCount;
    private final boolean needForm;

    CommandType(int argumentCount, boolean needForm) {
        this.argumentCount = argumentCount;
        this.needForm = needForm;
    }
}
