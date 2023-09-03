package sky.board.global.error.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sky.board.global.openapi.model.code;

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

    public ErrorDetail(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

