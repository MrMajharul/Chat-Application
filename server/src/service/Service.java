package service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import app.MessageType;
import model.Model_Client;
import model.Model_Create_Group;
import model.Model_File;
import model.Model_Group;
import model.Model_Login;
import model.Model_Message;
import model.Model_Message_Status;
import model.Model_Package_Sender;
import model.Model_Receive_Image;
import model.Model_Receive_Message;
import model.Model_Register;
import model.Model_History;
import model.Model_Update_Avatar;
import model.Model_Reques_File;
import model.Model_Send_Message;
import model.Model_User_Account;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

public class Service {
    
    private static Service instance;
    private SocketIOServer server;
    private ServiceUser serviceUser;
    private ServiceFIle serviceFile;
    private ServiceMessage serviceMessage;
    private ServiceGroup serviceGroup;
    private List<Model_Client> listClient;
    private JTextArea textArea;
    private final int PORT_NUMBER = 9999;
    
    public static Service getInstance(JTextArea textArea) {
        if (instance == null) {
            instance = new Service(textArea);
        }
        return instance;
    }
    
    private Service(JTextArea textArea) {
        this.textArea = textArea;
        serviceUser = new ServiceUser();
        serviceFile = new ServiceFIle();
        serviceMessage = new ServiceMessage();
        serviceGroup = new ServiceGroup();
        listClient = new ArrayList<>();
    }
    
    public void startServer() {
        Configuration config = new Configuration();
        config.setPort(PORT_NUMBER);
        server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient sioc) {
                textArea.append("One client connected\n");
            }
        });
        server.addEventListener("register", Model_Register.class, new DataListener<Model_Register>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Register t, AckRequest ar) throws Exception {
                Model_Message message = serviceUser.register(t);
                ar.sendAckData(message.isAction(), message.getMessage(), message.getData());
                if (message.isAction()) {
                    textArea.append("User has Register :" + t.getUserName() + " Pass :" + t.getPassword() + "\n");
                    server.getBroadcastOperations().sendEvent("list_user", (Model_User_Account) message.getData());
                    addClient(sioc, (Model_User_Account) message.getData());
                }
            }
        });
        server.addEventListener("login", Model_Login.class, new DataListener<Model_Login>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Login t, AckRequest ar) throws Exception {
                try {
                    Model_User_Account login = serviceUser.login(t);
                    if (login != null) {
                        ar.sendAckData(true, login);
                        addClient(sioc, login);
                        userConnect(login.getUserID());
                    } else {
                        ar.sendAckData(false);
                    }
                } catch (Exception e) {
                    // Never leave the client waiting (it shows an infinite loading UI otherwise)
                    ar.sendAckData(false);
                    if (textArea != null) {
                        textArea.append("Login error: " + e + "\n");
                    }
                }
            }
        });
        server.addEventListener("list_user", Integer.class, new DataListener<Integer>() {
            @Override
            public void onData(SocketIOClient sioc, Integer userID, AckRequest ar) throws Exception {
                try {
                    List<Model_User_Account> list = serviceUser.getUser(userID);
                    sioc.sendEvent("list_user", list.toArray());
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        });
        server.addEventListener("send_to_user", Model_Send_Message.class, new DataListener<Model_Send_Message>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Send_Message t, AckRequest ar) throws Exception {
                sendToClient(sioc, t, ar);
            }
        });
        server.addEventListener("send_to_group", Model_Send_Message.class, new DataListener<Model_Send_Message>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Send_Message t, AckRequest ar) throws Exception {
                try {
                    // Persist group message
                    ServiceMessage.SavedMessage saved = serviceMessage.saveMessage(t);
                    if (saved != null) {
                        ar.sendAckData(true, saved.getMessageID(), saved.getCreatedAt(), t.getClientId());
                    } else {
                        ar.sendAckData(false, 0, System.currentTimeMillis(), t.getClientId());
                    }
                    // Notify all group members
                    List<Integer> members = serviceGroup.getGroupMembers(t.getToUserID());
                    long createdAt = saved != null ? saved.getCreatedAt() : System.currentTimeMillis();
                    int messageID = saved != null ? saved.getMessageID() : 0;
                    int status = saved != null ? saved.getStatus() : 0;
                    for (int userID : members) {
                        for (Model_Client c : listClient) {
                            if (c.getUser().getUserID() == userID) {
                                Model_Receive_Message receive = new Model_Receive_Message(messageID, t.getMessageType(),
                                        t.getFromUserID(), t.getText(), null, createdAt, status,
                                        t.getReplyToMessageID(), t.getReplyUserName(), t.getReplyText());
                                receive.setGroupID(t.getToUserID());
                                c.getClient().sendEvent("receive_ms", receive);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    ar.sendAckData(false);
                    if (textArea != null) {
                        textArea.append("Send group error: " + e.getMessage() + "\n");
                        e.printStackTrace();
                    }
                }
            }
        });
        server.addEventListener("message_delivered", Model_Message_Status.class, new DataListener<Model_Message_Status>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Message_Status t, AckRequest ar) throws Exception {
                serviceMessage.updateStatus(t.getMessageID(), 1);
                notifySenderStatus(t.getFromUserID(), t.getMessageID(), 1);
            }
        });
        server.addEventListener("message_seen", Model_Message_Status.class, new DataListener<Model_Message_Status>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Message_Status t, AckRequest ar) throws Exception {
                serviceMessage.updateStatus(t.getMessageID(), 2);
                notifySenderStatus(t.getFromUserID(), t.getMessageID(), 2);
            }
        });
        server.addEventListener("load_history", Model_History.class, new DataListener<Model_History>() {
            @Override
            public void onData(SocketIOClient sioc, Model_History t, AckRequest ar) throws Exception {
                try {
                    ar.sendAckData(serviceMessage.getHistory(t.getFromUserID(), t.getToUserID(), t.getLimit()).toArray());
                } catch (Exception e) {
                    ar.sendAckData();
                    if (textArea != null) {
                        textArea.append("History error: " + e + "\n");
                    }
                }
            }
        });
        server.addEventListener("load_group_history", Model_History.class, new DataListener<Model_History>() {
            @Override
            public void onData(SocketIOClient sioc, Model_History t, AckRequest ar) throws Exception {
                try {
                    ar.sendAckData(serviceMessage.getGroupHistory(t.getToUserID(), t.getLimit()).toArray());
                } catch (Exception e) {
                    ar.sendAckData();
                    if (textArea != null) {
                        textArea.append("Group history error: " + e + "\n");
                    }
                }
            }
        });
        server.addEventListener("create_group", Model_Create_Group.class, new DataListener<Model_Create_Group>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Create_Group t, AckRequest ar) throws Exception {
                try {
                    if (t == null) {
                        throw new Exception("Create group data is null");
                    }
                    if (t.getGroupName() == null || t.getGroupName().trim().isEmpty()) {
                        throw new Exception("Group name cannot be empty");
                    }
                    if (t.getMembers() == null || t.getMembers().isEmpty()) {
                        throw new Exception("Group must have at least one member");
                    }
                    
                    Model_Group group = serviceGroup.createGroup(t);
                    if (group != null) {
                        textArea.append("Group created: " + group.getGroupName() + " (ID: " + group.getGroupID() + ")\n");
                        ar.sendAckData(true, group);
                        
                        // Notify all members about the new group
                        if (t.getMembers() != null) {
                            for (int userID : t.getMembers()) {
                                for (Model_Client c : listClient) {
                                    if (c.getUser().getUserID() == userID) {
                                        c.getClient().sendEvent("new_group", group);
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        ar.sendAckData(false);
                        textArea.append("Failed to create group: " + t.getGroupName() + "\n");
                    }
                } catch (Exception e) {
                    ar.sendAckData(false);
                    if (textArea != null) {
                        textArea.append("Create group error: " + e.getMessage() + "\n");
                        e.printStackTrace();
                    }
                }
            }
        });
        server.addEventListener("list_group", Integer.class, new DataListener<Integer>() {
            @Override
            public void onData(SocketIOClient sioc, Integer userID, AckRequest ar) throws Exception {
                try {
                    List<Model_Group> list = serviceGroup.getGroups(userID);
                    sioc.sendEvent("list_group", list.toArray());
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        });
        server.addEventListener("update_avatar", Model_Update_Avatar.class, new DataListener<Model_Update_Avatar>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Update_Avatar t, AckRequest ar) throws Exception {
                try {
                    Model_User_Account user = serviceUser.updateAvatar(t.getUserID(), t.getImageBase64());
                    if (user != null) {
                        ar.sendAckData(true, user);
                        server.getBroadcastOperations().sendEvent("user_update", user);
                    } else {
                        ar.sendAckData(false);
                    }
                } catch (Exception e) {
                    ar.sendAckData(false);
                    if (textArea != null) {
                        textArea.append("Avatar update error: " + e + "\n");
                    }
                }
            }
        });
        server.addEventListener("send_file", Model_Package_Sender.class, new DataListener<Model_Package_Sender>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Package_Sender t, AckRequest ar) throws Exception {
                try {
                    serviceFile.receiveFile(t);
                    if (t.isFinish()) {
                        ar.sendAckData(true);
                        Model_Receive_Image dataImage = new Model_Receive_Image();
                        dataImage.setFileID(t.getFileID());
                        Model_Send_Message message = serviceFile.closeFile(dataImage);
                        //  Send to client 'message'
                        sendTempFileToClient(message, dataImage);
                        
                    } else {
                        ar.sendAckData(true);
                    }
                } catch (IOException | SQLException e) {
                    ar.sendAckData(false);
                    e.printStackTrace();
                }
            }
        });
        server.addEventListener("get_file", Integer.class, new DataListener<Integer>() {
            @Override
            public void onData(SocketIOClient sioc, Integer t, AckRequest ar) throws Exception {
                Model_File file = serviceFile.initFile(t);
                long fileSize = serviceFile.getFileSize(t);
                ar.sendAckData(file.getFileExtension(), fileSize);
            }
        });
        server.addEventListener("reques_file", Model_Reques_File.class, new DataListener<Model_Reques_File>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Reques_File t, AckRequest ar) throws Exception {
                byte[] data = serviceFile.getFileData(t.getCurrentLength(), t.getFileID());
                if (data != null) {
                    ar.sendAckData(data);
                } else {
                    ar.sendAckData();
                }
            }
        });
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient sioc) {
                int userID = removeClient(sioc);
                if (userID != 0) {
                    //  removed
                    userDisconnect(userID);
                }
            }
        });
        server.start();
        textArea.append("Server has Start on port : " + PORT_NUMBER + "\n");
    }
    
    private void userConnect(int userID) {
        server.getBroadcastOperations().sendEvent("user_status", userID, true);
    }
    
    private void userDisconnect(int userID) {
        server.getBroadcastOperations().sendEvent("user_status", userID, false);
    }
    
    private void addClient(SocketIOClient client, Model_User_Account user) {
        listClient.add(new Model_Client(client, user));
    }
    
    private void sendToClient(SocketIOClient sender, Model_Send_Message data, AckRequest ar) {
        if (data.getMessageType() == MessageType.IMAGE.getValue() || data.getMessageType() == MessageType.FILE.getValue()) {
            try {
                Model_File file = serviceFile.addFileReceiver(data.getText());
                serviceFile.initFile(file, data);
                ar.sendAckData(file.getFileID());
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Persist text/emoji messages (types 1/2) so clients can load history later
            ServiceMessage.SavedMessage saved = serviceMessage.saveMessage(data);
            if (saved != null) {
                ar.sendAckData(true, saved.getMessageID(), saved.getCreatedAt(), data.getClientId());
            } else {
                ar.sendAckData(false, 0, System.currentTimeMillis(), data.getClientId());
            }
            for (Model_Client c : listClient) {
                if (c.getUser().getUserID() == data.getToUserID()) {
                    int messageID = saved != null ? saved.getMessageID() : 0;
                    long createdAt = saved != null ? saved.getCreatedAt() : System.currentTimeMillis();
                    int status = saved != null ? saved.getStatus() : 0;
                    c.getClient().sendEvent("receive_ms",
                            new Model_Receive_Message(messageID, data.getMessageType(), data.getFromUserID(),
                                    data.getText(), null, createdAt, status, data.getReplyToMessageID(),
                                    data.getReplyUserName(), data.getReplyText()));
                    break;
                }
            }
        }
    }

    private void notifySenderStatus(int fromUserID, int messageID, int status) {
        for (Model_Client c : listClient) {
            if (c.getUser().getUserID() == fromUserID) {
                c.getClient().sendEvent("message_status", messageID, status);
                break;
            }
        }
    }
    
    private void sendTempFileToClient(Model_Send_Message data, Model_Receive_Image dataImage) {
        Integer fileID = dataImage != null ? dataImage.getFileID() : null;
        ServiceMessage.SavedMessage saved = serviceMessage.saveMessage(data, fileID);
        int messageID = saved != null ? saved.getMessageID() : 0;
        long createdAt = saved != null ? saved.getCreatedAt() : System.currentTimeMillis();
        int status = saved != null ? saved.getStatus() : 0;
        String fileExtension = null;
        Long fileSize = null;
        try {
            if (fileID != null) {
                Model_File file = serviceFile.getFile(fileID);
                fileExtension = file.getFileExtension();
                fileSize = file.getFileSize();
                if (data.getMessageType() == MessageType.IMAGE.getValue()) {
                    if (file.getBlurHash() != null && file.getWidth() != null && file.getHeight() != null) {
                        dataImage = new Model_Receive_Image(fileID, file.getBlurHash(), file.getWidth(), file.getHeight());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("File info error: " + e);
        }
        for (Model_Client c : listClient) {
            if (c.getUser().getUserID() == data.getToUserID()) {
                c.getClient().sendEvent("receive_ms",
                        new Model_Receive_Message(messageID, data.getMessageType(), data.getFromUserID(), data.getText(),
                                dataImage, createdAt, status, data.getReplyToMessageID(), data.getReplyUserName(),
                                data.getReplyText(), fileID, fileExtension, fileSize));
                break;
            }
        }
    }
    
    public int removeClient(SocketIOClient client) {
        for (Model_Client d : listClient) {
            if (d.getClient() == client) {
                listClient.remove(d);
                return d.getUser().getUserID();
            }
        }
        return 0;
    }
    
    public List<Model_Client> getListClient() {
        return listClient;
    }
}
