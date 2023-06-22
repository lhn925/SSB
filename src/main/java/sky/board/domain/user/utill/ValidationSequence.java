package sky.board.domain.user.utill;

import jakarta.validation.GroupSequence;
import sky.board.domain.user.utill.ValidationGroups.NotBlankGroup;
import sky.board.domain.user.utill.ValidationGroups.PatternCheckGroup;
// validation 우선순위 설정
@GroupSequence({NotBlankGroup.class, PatternCheckGroup.class})
public interface ValidationSequence {

}
