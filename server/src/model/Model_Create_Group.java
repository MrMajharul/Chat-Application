package model;

import java.util.List;

public class Model_Create_Group {

    public String groupName;
    public int adminID;
    public List<Integer> members;

    public Model_Create_Group() {
    }

    public Model_Create_Group(String groupName, int adminID, List<Integer> members) {
        this.groupName = groupName;
        this.adminID = adminID;
        this.members = members;
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

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }
}
