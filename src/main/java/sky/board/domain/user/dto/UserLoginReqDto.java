package sky.board.domain.user.dto;


import lombok.Getter;

@Getter
public class UserLoginReqDto {
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }
}