package sky.Sss.domain.user.dto;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class Greeting {
    private String content;

    public Greeting(String content) {
        this.content = content;
    }
}
