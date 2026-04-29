package service;

import connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Model_Receive_Image;
import model.Model_Receive_Message;
import model.Model_Send_Message;

public class ServiceMessage {

    public ServiceMessage() {
        this.con = DatabaseConnection.getInstance().getConnection();
    }

    public SavedMessage saveMessage(Model_Send_Message data, Integer fileID) {
        try {
            PreparedStatement p = con.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
            p.setInt(1, data.getFromUserID());
            p.setInt(2, data.getToUserID());
            p.setInt(3, data.getMessageType());
            p.setString(4, data.getText());
            if (fileID == null) {
                p.setNull(5, java.sql.Types.INTEGER);
            } else {
                p.setInt(5, fileID);
            }
            p.setInt(6, 0);
            if (data.getReplyToMessageID() == null) {
                p.setNull(7, java.sql.Types.INTEGER);
            } else {
                p.setInt(7, data.getReplyToMessageID());
            }
            p.setString(8, data.getReplyUserName());
            p.setString(9, data.getReplyText());
            p.execute();
            ResultSet r = p.getGeneratedKeys();
            if (r.next()) {
                int messageID = r.getInt(1);
                r.close();
                p.close();
                PreparedStatement p2 = con.prepareStatement(SELECT_CREATED_AT);
                p2.setInt(1, messageID);
                ResultSet r2 = p2.executeQuery();
                long createdAt = System.currentTimeMillis();
                if (r2.next()) {
                    createdAt = r2.getTimestamp(1).getTime();
                }
                r2.close();
                p2.close();
                return new SavedMessage(messageID, createdAt, 0);
            }
            p.close();
            return null;
        } catch (SQLException e) {
            // Keep server running even if persistence fails
            System.err.println("Save message error: " + e);
            return null;
        }
    }

    public SavedMessage saveMessage(Model_Send_Message data) {
        return saveMessage(data, null);
    }

    public List<Model_Receive_Message> getHistory(int userA, int userB, int limit) throws SQLException {
        int safeLimit = limit;
        if (safeLimit <= 0) {
            safeLimit = 50;
        } else if (safeLimit > 200) {
            safeLimit = 200;
        }

        PreparedStatement p = con.prepareStatement(SELECT_HISTORY);
        p.setInt(1, userA);
        p.setInt(2, userB);
        p.setInt(3, userB);
        p.setInt(4, userA);
        p.setInt(5, safeLimit);

        ResultSet r = p.executeQuery();
        List<Model_Receive_Message> list = new ArrayList<>();
        while (r.next()) {
            int messageID = r.getInt(1);
            int messageType = r.getInt(2);
            int fromUserID = r.getInt(3);
            String text = r.getString(4);
            long createdAt = r.getTimestamp(5).getTime();
            int status = r.getInt(6);
            Integer replyTo = r.getObject(7) == null ? null : r.getInt(7);
            String replyUserName = r.getString(8);
            String replyText = r.getString(9);
            Integer fileID = r.getObject(10) == null ? null : r.getInt(10);
            String fileExtension = r.getString(11);
            String blurHash = r.getString(12);
            Integer width = r.getObject(13) == null ? null : r.getInt(13);
            Integer height = r.getObject(14) == null ? null : r.getInt(14);
            Long fileSize = r.getObject(15) == null ? null : r.getLong(15);
            Model_Receive_Image dataImage = null;
            if (fileID != null && blurHash != null && width != null && height != null) {
                dataImage = new Model_Receive_Image(fileID, blurHash, width, height);
            }
            list.add(new Model_Receive_Message(messageID, messageType, fromUserID, text, dataImage, createdAt, status,
                    replyTo, replyUserName, replyText, fileID, fileExtension, fileSize));
        }
        r.close();
        p.close();
        return list;
    }

    public List<Model_Receive_Message> getGroupHistory(int groupID, int limit) throws SQLException {
        int safeLimit = limit;
        if (safeLimit <= 0) {
            safeLimit = 50;
        } else if (safeLimit > 200) {
            safeLimit = 200;
        }

        PreparedStatement p = con.prepareStatement(SELECT_GROUP_HISTORY);
        p.setInt(1, groupID);
        p.setInt(2, groupID);
        p.setInt(3, safeLimit);

        ResultSet r = p.executeQuery();
        List<Model_Receive_Message> list = new ArrayList<>();
        while (r.next()) {
            int messageID = r.getInt(1);
            int messageType = r.getInt(2);
            int fromUserID = r.getInt(3);
            String text = r.getString(4);
            long createdAt = r.getTimestamp(5).getTime();
            int status = r.getInt(6);
            Integer replyTo = r.getObject(7) == null ? null : r.getInt(7);
            String replyUserName = r.getString(8);
            String replyText = r.getString(9);
            Integer fileID = r.getObject(10) == null ? null : r.getInt(10);
            String fileExtension = r.getString(11);
            String blurHash = r.getString(12);
            Integer width = r.getObject(13) == null ? null : r.getInt(13);
            Integer height = r.getObject(14) == null ? null : r.getInt(14);
            Long fileSize = r.getObject(15) == null ? null : r.getLong(15);
            Model_Receive_Image dataImage = null;
            if (fileID != null && blurHash != null && width != null && height != null) {
                dataImage = new Model_Receive_Image(fileID, blurHash, width, height);
            }
            Model_Receive_Message ms = new Model_Receive_Message(messageID, messageType, fromUserID, text, dataImage, createdAt,
                    status, replyTo, replyUserName, replyText, fileID, fileExtension, fileSize);
            ms.setGroupID(groupID);
            list.add(ms);
        }
        r.close();
        p.close();
        return list;
    }

    public void updateStatus(int messageID, int status) {
        try {
            PreparedStatement p = con.prepareStatement(UPDATE_STATUS);
            p.setInt(1, status);
            p.setInt(2, messageID);
            p.execute();
            p.close();
        } catch (SQLException e) {
            System.err.println("Update status error: " + e);
        }
    }

    private final String INSERT = "insert into messages (FromUserID, ToUserID, MessageType, Text, FileID, Status, ReplyToMessageID, ReplyUserName, ReplyText) values (?,?,?,?,?,?,?,?,?)";
    private final String SELECT_CREATED_AT = "select CreatedAt from messages where MessageID=?";
    private final String SELECT_HISTORY = "select t.MessageID, t.MessageType, t.FromUserID, t.Text, t.CreatedAt, t.Status, t.ReplyToMessageID, t.ReplyUserName, t.ReplyText, t.FileID, f.FileExtension, f.BlurHash, f.Width, f.Height, f.FileSize from (select MessageID, MessageType, FromUserID, Text, CreatedAt, Status, ReplyToMessageID, ReplyUserName, ReplyText, FileID from messages where ((FromUserID=? and ToUserID=?) or (FromUserID=? and ToUserID=?)) order by MessageID desc limit ?) t left join files f on f.FileID = t.FileID order by t.MessageID asc";
    private final String SELECT_GROUP_HISTORY = "select t.MessageID, t.MessageType, t.FromUserID, t.Text, t.CreatedAt, t.Status, t.ReplyToMessageID, t.ReplyUserName, t.ReplyText, t.FileID, f.FileExtension, f.BlurHash, f.Width, f.Height, f.FileSize from (select MessageID, MessageType, FromUserID, Text, CreatedAt, Status, ReplyToMessageID, ReplyUserName, ReplyText, FileID from messages where ToUserID=? and exists (select 1 from group_chat_member gm where gm.GroupID=? and gm.UserID=FromUserID) order by MessageID desc limit ?) t left join files f on f.FileID = t.FileID order by t.MessageID asc";
    private final String UPDATE_STATUS = "update messages set Status=? where MessageID=?";

    private final Connection con;

    public static class SavedMessage {

        public int getMessageID() {
            return messageID;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public int getStatus() {
            return status;
        }

        public SavedMessage(int messageID, long createdAt, int status) {
            this.messageID = messageID;
            this.createdAt = createdAt;
            this.status = status;
        }

        private final int messageID;
        private final long createdAt;
        private final int status;
    }
}
