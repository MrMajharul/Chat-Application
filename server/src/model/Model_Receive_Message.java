package model;

public class Model_Receive_Message {

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

    public Model_Receive_Message(int messageType, int fromUserID, String text, Model_Receive_Image dataImage) {
        this.messageType = messageType;
        this.fromUserID = fromUserID;
        this.text = text;
        this.dataImage = dataImage;
    }

    public Model_Receive_Message(int messageID, int messageType, int fromUserID, String text, Model_Receive_Image dataImage,
            long createdAt, int status, Integer replyToMessageID, String replyUserName, String replyText) {
        this.messageID = messageID;
        this.messageType = messageType;
        this.fromUserID = fromUserID;
        this.text = text;
        this.dataImage = dataImage;
        this.createdAt = createdAt;
        this.status = status;
        this.replyToMessageID = replyToMessageID;
        this.replyUserName = replyUserName;
        this.replyText = replyText;
    }

    public Model_Receive_Message(int messageID, int messageType, int fromUserID, String text, Model_Receive_Image dataImage,
            long createdAt, int status, Integer replyToMessageID, String replyUserName, String replyText, Integer fileID,
            String fileExtension, Long fileSize) {
        this(messageID, messageType, fromUserID, text, dataImage, createdAt, status, replyToMessageID, replyUserName,
                replyText);
        this.fileID = fileID;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
    }

    public Model_Receive_Message() {
    }

    private int messageType;
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
}
