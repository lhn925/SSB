package sky.board.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Result<T>{
    T Data;

    public Result(T data) {
        Data = data;
    }
}
