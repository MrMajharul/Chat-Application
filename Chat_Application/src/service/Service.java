package service;

import event.EventFileReceiver;
import event.PublicEvent;
import model.Model_File_Receiver;
import model.Model_File_Sender;
import model.Model_Receive_Message;
import model.Model_Send_Message;
import model.Model_User_Account;
import model.Model_Message_Status;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {

    private static Service instance;
    private Socket client;
    private final int PORT_NUMBER = 9999;
    private final String IP = "localhost";
    private Model_User_Account user;
    private List<Model_File_Sender> fileSender;
    private List<Model_File_Receiver> fileReceiver;
    private boolean appFocused = true;
    private Integer currentChatUserId;
    private Integer currentChatGroupId;
    private final Map<Integer, Integer> unreadUsers = new HashMap<>();
    private final Map<Integer, Integer> unreadGroups = new HashMap<>();

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    private Service() {
        fileSender = new ArrayList<>();
        fileReceiver = new ArrayList<>();
    }

    public void startServer() {
        try {
            client = IO.socket("http://" + IP + ":" + PORT_NUMBER);
            client.on("list_user", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    //  list user
                    List<Model_User_Account> users = new ArrayList<>();
                    for (Object o : os) {
                        Model_User_Account u = new Model_User_Account(o);
                        if (u.getUserID() != user.getUserID()) {
                            users.add(u);
                        }
                    }
                    PublicEvent.getInstance().getEventMenuLeft().newUser(users);
                }
            });
            client.on("list_group", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    List<model.Model_Group> groups = new ArrayList<>();
                    for (Object o : os) {
                        groups.add(new model.Model_Group(o));
                    }
                    PublicEvent.getInstance().getEventMenuLeft().newGroup(groups);
                }
            });
            client.on("new_group", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    List<model.Model_Group> groups = new ArrayList<>();
                    groups.add(new model.Model_Group(os[0]));
                    PublicEvent.getInstance().getEventMenuLeft().newGroup(groups);
                }
            });
            client.on("user_status", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    int userID = (Integer) os[0];
                    boolean status = (Boolean) os[1];
                    if (status) {
                        //  connect
                        PublicEvent.getInstance().getEventMenuLeft().userConnect(userID);
                    } else {
                        //  disconnect
                        PublicEvent.getInstance().getEventMenuLeft().userDisconnect(userID);
                    }
                }
            });
            client.on("receive_ms", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    Model_Receive_Message message = new Model_Receive_Message(os[0]);
                    updateUnreadState(message);
                    PublicEvent.getInstance().getEventChat().receiveMessage(message);
                    notifyIncoming(message);
                }
            });
            client.on("message_status", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    if (os.length >= 2) {
                        int messageID = (Integer) os[0];
                        int status = (Integer) os[1];
                        PublicEvent.getInstance().getEventChat().updateMessageStatus(messageID, status);
                    }
                }
            });
            client.on("user_update", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    if (os.length > 0) {
                        Model_User_Account u = new Model_User_Account(os[0]);
                        if (user != null && user.getUserID() == u.getUserID()) {
                            user.setImage(u.getImage());
                        }
                        PublicEvent.getInstance().getEventMenuLeft().updateUser(u);
                        PublicEvent.getInstance().getEventMain().updateUser(u);
                    }
                }
            });
            client.open();
        } catch (URISyntaxException e) {
            error(e);
        }
    }

    private void notifyIncoming(Model_Receive_Message message) {
        // User asked for notifications always, with preview + sound
        String sender = resolveUserName(message.getFromUserID());
        String preview = buildPreview(message);
        NotificationManager.notifyMessage(sender, preview, true, true, true);
    }

    private String resolveUserName(int userId) {
        try {
            if (PublicEvent.getInstance().getEventMenuLeft() != null) {
                for (Model_User_Account u : PublicEvent.getInstance().getEventMenuLeft().getUserList()) {
                    if (u.getUserID() == userId) {
                        return u.getUserName();
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return "User " + userId;
    }

    private String buildPreview(Model_Receive_Message message) {
        if (message.getMessageType() == app.MessageType.TEXT) {
            return message.getText();
        } else if (message.getMessageType() == app.MessageType.EMOJI) {
            return "[Emoji]";
        } else if (message.getMessageType() == app.MessageType.IMAGE) {
            return "[Image]";
        } else {
            return "[File]";
        }
    }

    public Model_File_Sender addFile(File file, Model_Send_Message message) throws IOException {
        Model_File_Sender data = new Model_File_Sender(file, client, message);
        message.setFile(data);
        fileSender.add(data);
        //  For send file one by one
        if (fileSender.size() == 1) {
            data.initSend();
        }
        return data;
    }

    public void fileSendFinish(Model_File_Sender data) throws IOException {
        fileSender.remove(data);
        if (!fileSender.isEmpty()) {
            //  Start send new file when old file sending finish
            fileSender.get(0).initSend();
        }
    }

    public void fileReceiveFinish(Model_File_Receiver data) throws IOException {
        fileReceiver.remove(data);
        if (!fileReceiver.isEmpty()) {
            fileReceiver.get(0).initReceive();
        }
    }

    public void addFileReceiver(int fileID, EventFileReceiver event) throws IOException {
        Model_File_Receiver data = new Model_File_Receiver(fileID, client, event);
        fileReceiver.add(data);
        if (fileReceiver.size() == 1) {
            data.initReceive();
        }
    }

    public Socket getClient() {
        return client;
    }

    public Model_User_Account getUser() {
        return user;
    }

    public void setUser(Model_User_Account user) {
        this.user = user;
        // Request user groups after login
        if (user != null && client != null && client.connected()) {
            client.emit("list_group", user.getUserID());
        }
    }

    public boolean isAppFocused() {
        return appFocused;
    }

    public void setAppFocused(boolean appFocused) {
        this.appFocused = appFocused;
    }

    public Integer getCurrentChatUserId() {
        return currentChatUserId;
    }

    public void setCurrentChatUserId(Integer currentChatUserId) {
        this.currentChatUserId = currentChatUserId;
    }

    public Integer getCurrentChatGroupId() {
        return currentChatGroupId;
    }

    public void setCurrentChatGroupId(Integer currentChatGroupId) {
        this.currentChatGroupId = currentChatGroupId;
    }

    public int getUnreadUserCount(int userId) {
        return unreadUsers.containsKey(userId) ? unreadUsers.get(userId) : 0;
    }

    public int getUnreadGroupCount(int groupId) {
        return unreadGroups.containsKey(groupId) ? unreadGroups.get(groupId) : 0;
    }

    public void clearUnreadUser(int userId) {
        unreadUsers.remove(userId);
        refreshUnreadBadges();
    }

    public void clearUnreadGroup(int groupId) {
        unreadGroups.remove(groupId);
        refreshUnreadBadges();
    }

    private void updateUnreadState(Model_Receive_Message message) {
        if (message.getGroupID() != null) {
            int groupId = message.getGroupID();
            if (currentChatGroupId == null || currentChatGroupId != groupId) {
                unreadGroups.put(groupId, getUnreadGroupCount(groupId) + 1);
                refreshUnreadBadges();
            }
        } else {
            int fromUserId = message.getFromUserID();
            if (currentChatUserId == null || currentChatUserId != fromUserId) {
                unreadUsers.put(fromUserId, getUnreadUserCount(fromUserId) + 1);
                refreshUnreadBadges();
            }
        }
    }

    private void refreshUnreadBadges() {
        if (PublicEvent.getInstance().getEventMenuLeft() != null) {
            PublicEvent.getInstance().getEventMenuLeft().refreshUnreadBadges();
        }
    }

    private void error(Exception e) {
        System.err.println(e);
    }
}
