package common.ru.itmo.se.interaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response implements Serializable {
    private ResponseCode responseCode;
    private String responseBody;
    @Override
    public String toString() {
        return "Response[" + responseCode + "; " + responseBody + "]";
    }
}
