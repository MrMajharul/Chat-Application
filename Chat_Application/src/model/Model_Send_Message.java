package model;

import app.MessageType;
import org.json.JSONException;
import org.json.JSONObject;

public class Model_Send_Message {

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
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

    public Model_File_Sender getFile() {
        return file;
    }

    public void setFile(Model_File_Sender file) {
        this.file = file;
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

    public Model_Send_Message(MessageType messageType, int fromUserID, int toUserID, String text) {
        this.messageType = messageType;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.text = text;
    }

    public Model_Send_Message() {
    }

    private MessageType messageType;
    private int fromUserID;
    private int toUserID;
    private String text;
    private Model_File_Sender file;
    private String clientId;
    private Integer replyToMessageID;
    private String replyUserName;
    private String replyText;

    public JSONObject toJsonObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("messageType", messageType.getValue());
            json.put("fromUserID", fromUserID);
            json.put("toUserID", toUserID);
            if (clientId != null) {
                json.put("clientId", clientId);
            }
            if (messageType == MessageType.FILE || messageType == MessageType.IMAGE) {
                json.put("text", file.getFileExtensions());
            } else {
                json.put("text", text);
            }
            if (replyToMessageID != null) {
                json.put("replyToMessageID", replyToMessageID);
            }
            if (replyUserName != null) {
                json.put("replyUserName", replyUserName);
            }
            if (replyText != null) {
                json.put("replyText", replyText);
            }
            return json;
        } catch (JSONException e) {
            return null;
        }
    }
}
