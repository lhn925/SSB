package sky.board.global.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public abstract class ErrorDetail {
    private String objectName;
    private String code;
    private String message;

    public ErrorDetail(String objectName, String code, String message) {
        this.objectName = objectName;
        this.code = code;
        this.message = message;
    }
}

