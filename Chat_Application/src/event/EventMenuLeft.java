package event;

import java.util.List;
import model.Model_User_Account;

public interface EventMenuLeft {

    public void newUser(List<Model_User_Account> users);

    public void userConnect(int userID);

    public void userDisconnect(int userID);

    public void updateUser(Model_User_Account user);

    public List<Model_User_Account> getUserList();

    public void newGroup(List<model.Model_Group> groups);

    public List<model.Model_Group> getGroupList();

    public void refreshUnreadBadges();
}
