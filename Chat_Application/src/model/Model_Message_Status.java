package model;

import org.json.JSONException;
import org.json.JSONObject;

public class Model_Message_Status {

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Model_Message_Status(int messageID, int fromUserID, int toUserID, int status) {
        this.messageID = messageID;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.status = status;
    }

    public Model_Message_Status() {
    }

    private int messageID;
    private int fromUserID;
    private int toUserID;
    private int status;

    public JSONObject toJsonObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("messageID", messageID);
            json.put("fromUserID", fromUserID);
            json.put("toUserID", toUserID);
            json.put("status", status);
            return json;
        } catch (JSONException e) {
            return null;
        }
    }
}
