package service;

import connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Model_Create_Group;
import model.Model_Group;

public class ServiceGroup {

    public ServiceGroup() {
        this.con = DatabaseConnection.getInstance().getConnection();
    }

    public Model_Group createGroup(Model_Create_Group data) throws SQLException {
        Model_Group group = null;
        if (con == null) {
            throw new SQLException("Database connection is null");
        }
        try {
            con.setAutoCommit(false);
            PreparedStatement p = con.prepareStatement(INSERT_GROUP, PreparedStatement.RETURN_GENERATED_KEYS);
            p.setString(1, data.getGroupName());
            p.setInt(2, data.getAdminID());
            int rowsAffected = p.executeUpdate();
            ResultSet r = p.getGeneratedKeys();
            if (r.next()) {
                int groupID = r.getInt(1);
                group = new Model_Group(groupID, data.getGroupName(), data.getAdminID());
                r.close();
                p.close();
                // Add Admin to members
                p = con.prepareStatement(INSERT_MEMBER);
                p.setInt(1, groupID);
                p.setInt(2, data.getAdminID());
                p.executeUpdate();
                p.close();
                // Add other members
                if (data.getMembers() != null) {
                    for (int userID : data.getMembers()) {
                        if (userID != data.getAdminID()) {
                            p = con.prepareStatement(INSERT_MEMBER);
                            p.setInt(1, groupID);
                            p.setInt(2, userID);
                            p.executeUpdate();
                            p.close();
                        }
                    }
                }
                con.commit();
            } else {
                r.close();
                p.close();
                con.rollback();
                throw new SQLException("Failed to insert group - no generated key returned");
            }
            con.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ignored) {
            }
            con.setAutoCommit(true);
            throw e;
        }
        return group;
    }

    public List<Model_Group> getGroups(int userID) throws SQLException {
        List<Model_Group> list = new ArrayList<>();
        PreparedStatement p = con.prepareStatement(SELECT_GROUPS);
        p.setInt(1, userID);
        ResultSet r = p.executeQuery();
        while (r.next()) {
            list.add(new Model_Group(r.getInt(1), r.getString(2), r.getInt(3)));
        }
        r.close();
        p.close();
        return list;
    }

    public List<Integer> getGroupMembers(int groupID) throws SQLException {
        List<Integer> list = new ArrayList<>();
        PreparedStatement p = con.prepareStatement(SELECT_MEMBERS_BY_GROUP);
        p.setInt(1, groupID);
        ResultSet r = p.executeQuery();
        while (r.next()) {
            list.add(r.getInt(1));
        }
        r.close();
        p.close();
        return list;
    }

    private final String INSERT_GROUP = "insert into group_chat (GroupName, AdminID) values (?,?)";
    private final String INSERT_MEMBER = "insert into group_chat_member (GroupID, UserID) values (?,?)";
    private final String SELECT_GROUPS = "select group_chat.GroupID, GroupName, AdminID from group_chat join group_chat_member using (GroupID) where UserID=?";
    private final String SELECT_MEMBERS_BY_GROUP = "select UserID from group_chat_member where GroupID=?";
    private final Connection con;
}
