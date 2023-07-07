package sky.board.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@NoArgsConstructor
public class Result<T>{
    T Data;

    public Result(T data) {
        Data = data;
    }

    public static ResponseEntity<ErrorResult> getErrorResult(ErrorResult errorResult) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }
}
