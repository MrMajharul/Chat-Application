package model;

public class Model_Send_Message {

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(int fromUserID) {
        this.fromUserID = fromUserID;
    }

    public int getToUserID() {
        return toUserID;
    }

    public void setToUserID(int toUserID) {
        this.toUserID = toUserID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getReplyToMessageID() {
        return replyToMessageID;
    }

    public void setReplyToMessageID(Integer replyToMessageID) {
        this.replyToMessageID = replyToMessageID;
    }

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public Model_Send_Message(int messageType, int fromUserID, int toUserID, String text) {
        this.messageType = messageType;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.text = text;
    }

    public Model_Send_Message() {
    }

    private int messageType;
    private int fromUserID;
    private int toUserID;
    private String text;
    private String clientId;
    private Integer replyToMessageID;
    private String replyUserName;
    private String replyText;
}
