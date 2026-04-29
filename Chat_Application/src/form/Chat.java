package form;

import component.Chat_Body;
import component.Chat_Bottom;
import component.Chat_Title;
import event.EventChat;
import event.PublicEvent;
import event.EventMenuLeft;
import io.socket.client.Ack;
import app.MessageType;
import model.Model_History;
import model.Model_Receive_Message;
import model.Model_Send_Message;
import model.Model_User_Account;
import net.miginfocom.swing.MigLayout;
import service.Service;
import model.Model_Message_Status;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class Chat extends javax.swing.JPanel {
    private Chat_Title chatTitle;
    private Chat_Body chatBody;
    private Chat_Bottom chatBottom;

    public Chat() {
        initComponents();
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx, filly", "0[fill]0", "0[]0[100%, fill]0[shrink 0]0"));
        setBackground(new java.awt.Color(22, 24, 29));
        chatTitle = new Chat_Title();
        chatBody = new Chat_Body();
        chatBottom = new Chat_Bottom();
        PublicEvent.getInstance().addEventChat(new EventChat() {
            @Override
            public void sendMessage(Model_Send_Message data) {
                JComponent comp = chatBody.addItemRight(data);
                attachMenu(comp, data, true);
            }

            @Override
            public void receiveMessage(Model_Receive_Message data) {
                Integer activeGroup = Service.getInstance().getCurrentChatGroupId();
                Integer incomingGroup = data.getGroupID();
                if (incomingGroup != null && activeGroup != null && activeGroup.intValue() == incomingGroup.intValue()) {
                    if (data.getFromUserID() != Service.getInstance().getUser().getUserID()) {
                        JComponent comp = chatBody.addItemLeft(data, resolveSenderName(data.getFromUserID()));
                        attachMenu(comp, data, false);
                    }
                } else if (chatTitle.getUser() != null && chatTitle.getUser().getUserID() == data.getFromUserID()) {
                    JComponent comp = chatBody.addItemLeft(data);
                    attachMenu(comp, data, false);
                }
                if (data.getMessageID() > 0) {
                    Service.getInstance().getClient().emit("message_delivered",
                            new Model_Message_Status(data.getMessageID(), data.getFromUserID(),
                                    Service.getInstance().getUser().getUserID(), 1).toJsonObject());
                    if ((incomingGroup != null && activeGroup != null && activeGroup.intValue() == incomingGroup.intValue())
                            || (chatTitle.getUser() != null && chatTitle.getUser().getUserID() == data.getFromUserID())) {
                        Service.getInstance().getClient().emit("message_seen",
                                new Model_Message_Status(data.getMessageID(), data.getFromUserID(),
                                        Service.getInstance().getUser().getUserID(), 2).toJsonObject());
                    }
                }
            }

            @Override
            public void updateMessageStatus(int messageID, int status) {
                chatBody.updateMessageStatus(messageID, status);
            }

            @Override
            public void messageAcked(String clientId, int messageID, long createdAt, int status) {
                chatBody.updateMessageFromAck(clientId, messageID, createdAt, status);
            }
        });
        add(chatTitle, "wrap");
        add(chatBody, "wrap");
        add(chatBottom, "h ::50%");
    }

    public void setUser(Model_User_Account user) {
        chatTitle.setUserName(user);
        chatBottom.setUser(user);
        chatBody.showLoadingState();
        Service.getInstance().setCurrentChatUserId(user.getUserID());
        Service.getInstance().setCurrentChatGroupId(null);
        Service.getInstance().clearUnreadUser(user.getUserID());
        EventMenuLeft menuLeft = PublicEvent.getInstance().getEventMenuLeft();
        if (menuLeft != null) {
            menuLeft.refreshUnreadBadges();
        }

        Model_User_Account currentUser = Service.getInstance().getUser();
        if (currentUser != null) {
            Model_History req = new Model_History(currentUser.getUserID(), user.getUserID(), 50);
            Service.getInstance().getClient().emit("load_history", req.toJsonObject(), new Ack() {
                @Override
                public void call(Object... os) {
                    if (os == null || os.length == 0) {
                        chatBody.showConversationEmptyState();
                        return;
                    }
                    int lastFromMessageId = 0;
                    int loaded = 0;
                    for (Object o : os) {
                        try {
                            Model_Receive_Message ms = new Model_Receive_Message(o);
                            if (ms.getFromUserID() == currentUser.getUserID()) {
                                JComponent comp = chatBody.addHistoryRight(ms);
                                attachMenu(comp, ms, true);
                            } else {
                                JComponent comp = chatBody.addItemLeft(ms);
                                attachMenu(comp, ms, false);
                                if (ms.getMessageID() > lastFromMessageId) {
                                    lastFromMessageId = ms.getMessageID();
                                }
                            }
                            Service.getInstance().recordUserPreview(user.getUserID(), ms.getText(), ms.getCreatedAt());
                            loaded++;
                        } catch (Exception e) {
                            // ignore malformed history item
                        }
                    }
                    if (loaded == 0) {
                        chatBody.showConversationEmptyState();
                    }
                    chatBody.scrollToBottomNow();
                    if (lastFromMessageId > 0) {
                        Service.getInstance().getClient().emit("message_seen",
                                new Model_Message_Status(lastFromMessageId, user.getUserID(), currentUser.getUserID(), 2).toJsonObject());
                    }
                }
            });
        }
    }

    public void setGroup(model.Model_Group group) {
        chatTitle.setGroupName(group.getGroupName(), "Group ID: " + group.getGroupID());
        chatBody.showLoadingState();
        chatBottom.setUser(null);
        chatBottom.setGroup(group);
        Service.getInstance().setCurrentChatUserId(null);
        Service.getInstance().setCurrentChatGroupId(group.getGroupID());
        Service.getInstance().clearUnreadGroup(group.getGroupID());
        EventMenuLeft menuLeft = PublicEvent.getInstance().getEventMenuLeft();
        if (menuLeft != null) {
            menuLeft.refreshUnreadBadges();
        }
        Model_User_Account currentUser = Service.getInstance().getUser();
        if (currentUser != null) {
            Model_History req = new Model_History(currentUser.getUserID(), group.getGroupID(), 80);
            Service.getInstance().getClient().emit("load_group_history", req.toJsonObject(), new Ack() {
                @Override
                public void call(Object... os) {
                    if (os == null || os.length == 0) {
                        chatBody.showConversationEmptyState();
                        return;
                    }
                    int loaded = 0;
                    for (Object o : os) {
                        try {
                            Model_Receive_Message ms = new Model_Receive_Message(o);
                            ms.setGroupID(group.getGroupID());
                            if (ms.getFromUserID() == currentUser.getUserID()) {
                                JComponent comp = chatBody.addHistoryRight(ms);
                                attachMenu(comp, ms, true);
                            } else {
                                JComponent comp = chatBody.addItemLeft(ms, resolveSenderName(ms.getFromUserID()));
                                attachMenu(comp, ms, false);
                            }
                            Service.getInstance().recordGroupPreview(group.getGroupID(), ms.getText(), ms.getCreatedAt());
                            loaded++;
                        } catch (Exception ignore) {
                        }
                    }
                    if (loaded == 0) {
                        chatBody.showConversationEmptyState();
                    }
                    chatBody.scrollToBottomNow();
                }
            });
        }
    }

    private void attachMenu(JComponent comp, Model_Send_Message data, boolean isRight) {
        if (comp == null) {
            return;
        }
        JPopupMenu menu = new JPopupMenu();
        JMenuItem reply = new JMenuItem("Reply");
        reply.addActionListener(e -> chatBottom.setReply(isRight ? Service.getInstance().getUser().getUserName() : chatTitle.getUser().getUserName(),
                data.getText(), null));
        JMenuItem forward = new JMenuItem("Forward");
        forward.addActionListener(e -> forwardMessage(data.getText()));
        menu.add(reply);
        menu.add(forward);
        comp.setComponentPopupMenu(menu);
    }

    private void attachMenu(JComponent comp, Model_Receive_Message data, boolean isRight) {
        if (comp == null) {
            return;
        }
        String fromName = isRight ? Service.getInstance().getUser().getUserName() : resolveSenderName(data.getFromUserID());
        JPopupMenu menu = new JPopupMenu();
        JMenuItem reply = new JMenuItem("Reply");
        reply.addActionListener(e -> chatBottom.setReply(fromName, data.getText(), data.getMessageID()));
        JMenuItem forward = new JMenuItem("Forward");
        forward.addActionListener(e -> forwardMessage(data.getText()));
        menu.add(reply);
        menu.add(forward);
        comp.setComponentPopupMenu(menu);
    }

    private void forwardMessage(String text) {
        List<Model_User_Account> users = PublicEvent.getInstance().getEventMenuLeft().getUserList();
        if (users == null || users.isEmpty()) {
            return;
        }
        String[] names = users.stream().map(Model_User_Account::getUserName).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "Forward to:", "Forward",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
        if (selected != null) {
            for (Model_User_Account u : users) {
                if (selected.equals(u.getUserName())) {
                    Model_Send_Message message = new Model_Send_Message(MessageType.TEXT,
                            Service.getInstance().getUser().getUserID(), u.getUserID(), text);
                    chatBottom.sendExternal(message);
                    break;
                }
            }
        }
    }

    public void updateUser(Model_User_Account user) {
        chatTitle.updateUser(user);
    }

    private String resolveSenderName(int userId) {
        EventMenuLeft menuLeft = PublicEvent.getInstance().getEventMenuLeft();
        if (menuLeft != null && menuLeft.getUserList() != null) {
            for (Model_User_Account u : menuLeft.getUserList()) {
                if (u.getUserID() == userId) {
                    return u.getUserName();
                }
            }
        }
        return "User " + userId;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setBackground(new java.awt.Color(22, 24, 29));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 727, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 681, Short.MAX_VALUE));
    }
}
// Variables declaration - do not modify // End of variables declaration }
