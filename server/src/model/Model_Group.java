package model;

public class Model_Group {

    public int groupID;
    public String groupName;
    public int adminID;

    public Model_Group() {
    }

    public Model_Group(int groupID, String groupName, int adminID) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.adminID = adminID;
    }

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
}
