package model;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Model_Create_Group {

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

    public Model_Create_Group(String groupName, int adminID, List<Integer> members) {
        this.groupName = groupName;
        this.adminID = adminID;
        this.members = members;
    }

    public Model_Create_Group() {
    }

    private String groupName;
    private int adminID;
    private List<Integer> members;

    public JSONObject toJsonObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("groupName", groupName);
            json.put("adminID", adminID);
            JSONArray array = new JSONArray();
            for (Integer id : members) {
                array.put(id);
            }
            json.put("members", array);
            return json;
        } catch (JSONException e) {
            return null;
        }
    }
}
