package helpers;

public class ChatStructure {
    private String nickname , message , messageType, messageTime;

    public ChatStructure(String nickname, String message, String messageType, String messageTime) {
        this.nickname = nickname;
        this.message = message;
        this.messageType = messageType;
        this.messageTime = messageTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
