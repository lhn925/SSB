package sky.Sss.domain.user.model;

import lombok.Getter;

@Getter
public enum UserGrade {
    ADMIN("ADMIN"), USER("USER"), ANONYMOUS("ANONYMOUS");
    private String description;

    UserGrade(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getRole() {
        return "ROLE_" + description;
    }
}
