package sky.board.domain.user.model;

import lombok.Getter;

@Getter
public enum UserGrade {
    ADMIN("ADMIN"), MANAGER("MANAGER"), MEMBER("MEMBER");
    private String description;

    UserGrade(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
