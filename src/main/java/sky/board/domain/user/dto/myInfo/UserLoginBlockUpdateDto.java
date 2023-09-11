package sky.board.domain.user.dto.myInfo;


import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginBlockUpdateDto implements Serializable {

    /**
     * lombok에서 제공하는 @Getter 혹은 @Setter 어노테이션을 사용 할 경우 자동으로 getter/setter 메서드를 생성해주는데
     * 이 때 boolean 타입의 변수에 붙는 prefix는 get이 아닌 is이므로 @RequestBody에서 찾을 수 없어 바인딩 되지 않아 발생하는 문제가 있다
     */
    private Boolean isLoginBlocked;
}
