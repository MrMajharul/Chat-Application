package model;

import app.MessageType;
import org.json.JSONException;
import org.json.JSONObject;

public class Model_Receive_Message {

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Model_Receive_Image getDataImage() {
        return dataImage;
    }

    public void setDataImage(Model_Receive_Image dataImage) {
        this.dataImage = dataImage;
    }

    public Integer getFileID() {
        return fileID;
    }

    public void setFileID(Integer fileID) {
        this.fileID = fileID;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Model_Receive_Message(Object json) {
        JSONObject obj = (JSONObject) json;
        try {
            messageType = MessageType.toMessageType(obj.getInt("messageType"));
            fromUserID = obj.getInt("fromUserID");
            text = obj.getString("text");
            if (!obj.isNull("messageID")) {
                messageID = obj.getInt("messageID");
            }
            if (!obj.isNull("createdAt")) {
                createdAt = obj.getLong("createdAt");
            }
            if (!obj.isNull("status")) {
                status = obj.getInt("status");
            }
            if (!obj.isNull("replyToMessageID")) {
                replyToMessageID = obj.getInt("replyToMessageID");
            }
            if (!obj.isNull("replyUserName")) {
                replyUserName = obj.getString("replyUserName");
            }
            if (!obj.isNull("replyText")) {
                replyText = obj.getString("replyText");
            }
            if (!obj.isNull("fileID")) {
                fileID = obj.getInt("fileID");
            }
            if (!obj.isNull("fileExtension")) {
                fileExtension = obj.getString("fileExtension");
            }
            if (!obj.isNull("fileSize")) {
                fileSize = obj.getLong("fileSize");
            }
            if (!obj.isNull("groupID")) {
                groupID = obj.getInt("groupID");
            }
            if (!obj.isNull("dataImage")) {
                dataImage = new Model_Receive_Image(obj.get("dataImage"));
            }
        } catch (JSONException e) {
            System.err.println(e);
        }
    }

    private MessageType messageType;
    private int fromUserID;
    private String text;
    private Model_Receive_Image dataImage;
    private int messageID;
    private long createdAt;
    private int status;
    private Integer replyToMessageID;
    private String replyUserName;
    private String replyText;
    private Integer fileID;
    private String fileExtension;
    private Long fileSize;
    private Integer groupID;

    public JSONObject toJsonObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("messageType", messageType.getValue());
            json.put("fromUserID", fromUserID);
            json.put("text", text);
            if (messageID > 0) {
                json.put("messageID", messageID);
            }
            if (createdAt > 0) {
                json.put("createdAt", createdAt);
            }
            json.put("status", status);
            if (replyToMessageID != null) {
                json.put("replyToMessageID", replyToMessageID);
            }
            if (replyUserName != null) {
                json.put("replyUserName", replyUserName);
            }
            if (replyText != null) {
                json.put("replyText", replyText);
            }
            if (fileID != null) {
                json.put("fileID", fileID);
            }
            if (fileExtension != null) {
                json.put("fileExtension", fileExtension);
            }
            if (fileSize != null) {
                json.put("fileSize", fileSize);
            }
            if (groupID != null) {
                json.put("groupID", groupID);
            }
            if (dataImage != null) {
                json.put("dataImage", dataImage.toJsonObject());
            }
            return json;
        } catch (JSONException e) {
            return null;
        }
    }
}
