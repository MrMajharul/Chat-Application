package form;

import component.Chat_Body;
import component.Chat_Bottom;
import component.Chat_Title;
import event.EventChat;
import event.PublicEvent;
import io.socket.client.Ack;
import app.MessageType;
import model.Model_History;
import model.Model_Receive_Message;
import model.Model_Send_Message;
import model.Model_User_Account;
import net.miginfocom.swing.MigLayout;
import service.Service;

public class Chat extends javax.swing.JPanel {
    private Chat_Title chatTitle;
    private Chat_Body chatBody;
    private Chat_Bottom chatBottom;

    public Chat() {
        initComponents();
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx", "0[fill]0", "0[]0[100%, fill]0[shrink 0]0"));
        chatTitle = new Chat_Title();
        chatBody = new Chat_Body();
        chatBottom = new Chat_Bottom();
        PublicEvent.getInstance().addEventChat(new EventChat() {
            @Override
            public void sendMessage(Model_Send_Message data) {
                chatBody.addItemRight(data);
            }

            @Override
            public void receiveMessage(Model_Receive_Message data) {
                if (chatTitle.getUser().getUserID() == data.getFromUserID()) {
                    chatBody.addItemLeft(data);
                }
            }
        });
        add(chatTitle, "wrap");
        add(chatBody, "wrap");
        add(chatBottom, "h ::50%");
    }

    public void setUser(Model_User_Account user) {
        chatTitle.setUserName(user);
        chatBottom.setUser(user);
        chatBody.clearChat();

        Model_User_Account currentUser = Service.getInstance().getUser();
        if (currentUser != null) {
            Model_History req = new Model_History(currentUser.getUserID(), user.getUserID(), 50);
            Service.getInstance().getClient().emit("load_history", req.toJsonObject(), new Ack() {
                @Override
                public void call(Object... os) {
                    if (os == null || os.length == 0) {
                        return;
                    }
                    for (Object o : os) {
                        try {
                            Model_Receive_Message ms = new Model_Receive_Message(o);
                            if (ms.getMessageType() == MessageType.TEXT || ms.getMessageType() == MessageType.EMOJI) {
                                if (ms.getFromUserID() == currentUser.getUserID()) {
                                    chatBody.addItemRight(new Model_Send_Message(ms.getMessageType(), currentUser.getUserID(), user.getUserID(), ms.getText()));
                                } else {
                                    chatBody.addItemLeft(ms);
                                }
                            }
                        } catch (Exception e) {
                            // ignore malformed history item
                        }
                    }
                    chatBody.scrollToBottomNow();
                }
            });
        }
    }

    public void updateUser(Model_User_Account user) {
        chatTitle.updateUser(user);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setBackground(new java.awt.Color(46, 46, 46));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 727, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 681, Short.MAX_VALUE));
    }
}
// Variables declaration - do not modify // End of variables declaration }
