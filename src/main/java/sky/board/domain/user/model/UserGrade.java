package sky.board.domain.user.model;

import lombok.Getter;

@Getter
public enum UserGrade {
    ADMIN("ADMIN"), MANAGER("MANAGER"), USER("USER");
    private String description;

    UserGrade(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
