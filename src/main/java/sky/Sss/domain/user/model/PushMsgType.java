package sky.Sss.domain.user.model;

public enum PushMsgType {
    LIKES("LIKES"),REPLY("REPLY"),FOLLOW("FOLLOW"),REPOST("REPOST");

    private String type;
    PushMsgType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
