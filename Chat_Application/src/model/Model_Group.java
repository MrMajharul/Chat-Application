package model;

import org.json.JSONException;
import org.json.JSONObject;

public class Model_Group {

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public Model_Group(int groupID, String groupName, int adminID) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.adminID = adminID;
    }

    public Model_Group(Object json) {
        JSONObject obj = (JSONObject) json;
        try {
            groupID = obj.getInt("groupID");
            groupName = obj.getString("groupName");
            adminID = obj.getInt("adminID");
        } catch (JSONException e) {
            System.err.println(e);
        }
    }

    public Model_Group() {
    }

    private int groupID;
    private String groupName;
    private int adminID;
}
