package service;

import connection.DatabaseConnection;
import model.Model_Receive_Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceMessage {

    public ServiceMessage() {
        this.con = DatabaseConnection.getInstance().getConnection();
    }

    public void saveMessage(int messageType, int fromUserID, int toUserID, String text, Integer fileID) {
        try {
            PreparedStatement p = con.prepareStatement(INSERT);
            p.setInt(1, fromUserID);
            p.setInt(2, toUserID);
            p.setInt(3, messageType);
            p.setString(4, text);
            if (fileID == null) {
                p.setNull(5, java.sql.Types.INTEGER);
            } else {
                p.setInt(5, fileID);
            }
            p.execute();
            p.close();
        } catch (SQLException e) {
            // Keep server running even if persistence fails
            System.err.println("Save message error: " + e);
        }
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
            int messageType = r.getInt(1);
            int fromUserID = r.getInt(2);
            String text = r.getString(3);
            list.add(new Model_Receive_Message(messageType, fromUserID, text, null));
        }
        r.close();
        p.close();
        return list;
    }

    private final String INSERT = "insert into messages (FromUserID, ToUserID, MessageType, Text, FileID) values (?,?,?,?,?)";
    private final String SELECT_HISTORY = "select MessageType, FromUserID, Text from (select MessageID, MessageType, FromUserID, Text from messages where ((FromUserID=? and ToUserID=?) or (FromUserID=? and ToUserID=?)) and MessageType in (1,2) order by MessageID desc limit ?) t order by t.MessageID asc";

    private final Connection con;
}
