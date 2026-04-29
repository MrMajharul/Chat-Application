package component;

import com.formdev.flatlaf.FlatClientProperties;
import app.MessageType;
import emoji.Emogi;
import model.Model_Receive_Message;
import model.Model_Send_Message;
import java.awt.Adjustable;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;
import swing.RoundedPanel;

public class Chat_Body extends javax.swing.JPanel {

    public Chat_Body() {
        initComponents();
        init();
        messageMap = new HashMap<>();
        pendingMap = new HashMap<>();
        itemTimeMap = new HashMap<>();
    }

    private void init() {
        body.setLayout(new MigLayout("fillx", "", "5[bottom]5"));
        body.setBackground(new Color(24, 26, 31));
        showEmptyState("Select a chat to start messaging", "Pick a user or group from the left panel.");
        sp.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "width:5;"
                + "background:null;"
                + "trackInsets:5,0,5,0;"
                + "thumbInsets:5,0,5,0;");
        sp.getVerticalScrollBar().setUnitIncrement(10);
    }

    public javax.swing.JComponent addItemLeft(Model_Receive_Message data) {
        clearEmptyState();
        if (data.getMessageType() == MessageType.TEXT) {
            Chat_Left item = new Chat_Left();
            item.setText(data.getText());
            applyReply(item, data);
            String time = formatTime(data.getCreatedAt());
            item.setTime(time);
            body.add(item, "wrap, w 100::80%");
            registerMessage(item.getChatItem(), data.getMessageID(), time);
            return item;
        } else if (data.getMessageType() == MessageType.EMOJI) {
            Chat_Left item = new Chat_Left();
            item.setEmoji(Emogi.getInstance().getImoji(Integer.valueOf(data.getText())).getIcon());
            applyReply(item, data);
            String time = formatTime(data.getCreatedAt());
            item.setTime(time);
            body.add(item, "wrap, w 100::80%");
            registerMessage(item.getChatItem(), data.getMessageID(), time);
            return item;
        } else if (data.getMessageType() == MessageType.FILE) {
            Chat_Left item = new Chat_Left();
            String fileName = buildFileName(data);
            String sizeText = formatFileSize(data.getFileSize());
            item.setFile(fileName, sizeText);
            applyReply(item, data);
            String time = formatTime(data.getCreatedAt());
            item.setTime(time);
            body.add(item, "wrap, w 100::80%");
            registerMessage(item.getChatItem(), data.getMessageID(), time);
            return item;
        } else if (data.getMessageType() == MessageType.IMAGE) {
            Chat_Left item = new Chat_Left();
            item.setText("");
            item.setImage(data.getDataImage());
            String time = formatTime(data.getCreatedAt());
            item.setTime(time);
            body.add(item, "wrap, w 100::80%");
            registerMessage(item.getChatItem(), data.getMessageID(), time);
            return item;
        }
        repaint();
        revalidate();
        return null;
    }

    public void addItemLeft(String text, String user, String[] image) {
        clearEmptyState();
        Chat_Left_With_Profile item = new Chat_Left_With_Profile();
        item.setText(text);
        item.setImage(image);
        item.setTime();
        item.setUserProfile(user);
        body.add(item, "wrap, w 100::80%");
        //  ::80% set max with 80%
        body.repaint();
        body.revalidate();
    }

    public void addItemFile(String text, String user, String fileName, String fileSize) {
        clearEmptyState();
        Chat_Left_With_Profile item = new Chat_Left_With_Profile();
        item.setText(text);
        item.setFile(fileName, fileSize);
        item.setTime();
        item.setUserProfile(user);
        body.add(item, "wrap, w 100::80%");
        //  ::80% set max with 80%
        body.repaint();
        body.revalidate();
    }

    public javax.swing.JComponent addItemRight(Model_Send_Message data) {
        clearEmptyState();
        if (data.getMessageType() == MessageType.TEXT) {
            Chat_Right item = new Chat_Right();
            item.setText(data.getText());
            if (data.getReplyUserName() != null && data.getReplyText() != null) {
                item.setReply(data.getReplyUserName(), data.getReplyText());
            }
            String time = formatTime(System.currentTimeMillis());
            item.setStatus(0, time);
            body.add(item, "wrap, al right, w 100::80%");
            registerPending(data.getClientId(), item.getChatItem());
            return item;
        } else if (data.getMessageType() == MessageType.EMOJI) {
            Chat_Right item = new Chat_Right();
            item.setEmoji(Emogi.getInstance().getImoji(Integer.valueOf(data.getText())).getIcon());
            if (data.getReplyUserName() != null && data.getReplyText() != null) {
                item.setReply(data.getReplyUserName(), data.getReplyText());
            }
            String time = formatTime(System.currentTimeMillis());
            item.setStatus(0, time);
            body.add(item, "wrap, al right, w 100::80%");
            registerPending(data.getClientId(), item.getChatItem());
            return item;
        } else if (data.getMessageType() == MessageType.IMAGE) {
            Chat_Right item = new Chat_Right();
            item.setText("");
            item.setImage(data.getFile());
            String time = formatTime(System.currentTimeMillis());
            item.setStatus(0, time);
            body.add(item, "wrap, al right, w 100::80%");
            registerPending(data.getClientId(), item.getChatItem());
            return item;

        }
        repaint();
        revalidate();
        scrollToBottom();
        return null;
    }

    public javax.swing.JComponent addHistoryRight(Model_Receive_Message data) {
        clearEmptyState();
        if (data.getMessageType() == MessageType.TEXT) {
            Chat_Right item = new Chat_Right();
            item.setText(data.getText());
            if (data.getReplyUserName() != null && data.getReplyText() != null) {
                item.setReply(data.getReplyUserName(), data.getReplyText());
            }
            String time = formatTime(data.getCreatedAt());
            item.setStatus(data.getStatus(), time);
            body.add(item, "wrap, al right, w 100::80%");
            registerMessage(item.getChatItem(), data.getMessageID(), time);
            return item;
        } else if (data.getMessageType() == MessageType.EMOJI) {
            Chat_Right item = new Chat_Right();
            item.setEmoji(Emogi.getInstance().getImoji(Integer.valueOf(data.getText())).getIcon());
            if (data.getReplyUserName() != null && data.getReplyText() != null) {
                item.setReply(data.getReplyUserName(), data.getReplyText());
            }
            String time = formatTime(data.getCreatedAt());
            item.setStatus(data.getStatus(), time);
            body.add(item, "wrap, al right, w 100::80%");
            registerMessage(item.getChatItem(), data.getMessageID(), time);
            return item;
        } else if (data.getMessageType() == MessageType.IMAGE) {
            Chat_Right item = new Chat_Right();
            item.setText("");
            item.setImage(data.getDataImage());
            String time = formatTime(data.getCreatedAt());
            item.setStatus(data.getStatus(), time);
            body.add(item, "wrap, al right, w 100::80%");
            registerMessage(item.getChatItem(), data.getMessageID(), time);
            return item;
        } else if (data.getMessageType() == MessageType.FILE) {
            Chat_Right item = new Chat_Right();
            String fileName = buildFileName(data);
            String sizeText = formatFileSize(data.getFileSize());
            item.setFile(fileName, sizeText);
            if (data.getReplyUserName() != null && data.getReplyText() != null) {
                item.setReply(data.getReplyUserName(), data.getReplyText());
            }
            String time = formatTime(data.getCreatedAt());
            item.setStatus(data.getStatus(), time);
            body.add(item, "wrap, al right, w 100::80%");
            registerMessage(item.getChatItem(), data.getMessageID(), time);
            return item;
        }
        return null;
    }

    public void updateMessageStatus(int messageID, int status) {
        Chat_Item item = messageMap.get(messageID);
        if (item != null) {
            String time = formatTime(System.currentTimeMillis());
            itemTimeMap.put(messageID, time);
            item.setStatus(status, time);
            repaint();
            revalidate();
        }
    }

    public void updateMessageFromAck(String clientId, int messageID, long createdAt, int status) {
        if (clientId == null) {
            return;
        }
        Chat_Item item = pendingMap.remove(clientId);
        if (item != null) {
            messageMap.put(messageID, item);
            itemTimeMap.put(messageID, formatTime(createdAt));
            item.setStatus(status, formatTime(createdAt));
        }
    }

    public void addItemFileRight(String text, String fileName, String fileSize) {
        clearEmptyState();
        Chat_Right item = new Chat_Right();
        item.setText(text);
        item.setFile(fileName, fileSize);
        body.add(item, "wrap, al right, w 100::80%");
        //  ::80% set max with 80%
        body.repaint();
        body.revalidate();
    }

    public void addDate(String date) {
        clearEmptyState();
        Chat_Date item = new Chat_Date();
        item.setDate(date);
        body.add(item, "wrap, al center");
        body.repaint();
        body.revalidate();
    }

    public void clearChat() {
        body.removeAll();
        messageMap.clear();
        pendingMap.clear();
        itemTimeMap.clear();
        showEmptyState("No messages yet", "Send the first message to start the conversation.");
        repaint();
        revalidate();
    }

    private void showEmptyState(String title, String hint) {
        emptyStatePanel = new JPanel();
        emptyStatePanel.setOpaque(false);
        emptyStatePanel.setLayout(new BoxLayout(emptyStatePanel, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel();
        icon.setAlignmentX(CENTER_ALIGNMENT);
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setIcon(new ImageIcon(getClass().getResource("/images/icon-chat-active.png")));

        emptyStateLabel = new JLabel(title, SwingConstants.CENTER);
        emptyStateLabel.setAlignmentX(CENTER_ALIGNMENT);
        emptyStateLabel.setForeground(new Color(170, 178, 194));
        emptyStateLabel.putClientProperty(FlatClientProperties.STYLE, "font:+2 bold");

        JLabel hintLabel = new JLabel(hint, SwingConstants.CENTER);
        hintLabel.setAlignmentX(CENTER_ALIGNMENT);
        hintLabel.setForeground(new Color(137, 146, 163));
        hintLabel.putClientProperty(FlatClientProperties.STYLE, "font:+0");

        emptyStatePanel.add(Box.createVerticalGlue());
        emptyStatePanel.add(icon);
        emptyStatePanel.add(Box.createVerticalStrut(10));
        emptyStatePanel.add(emptyStateLabel);
        emptyStatePanel.add(Box.createVerticalStrut(4));
        emptyStatePanel.add(hintLabel);
        emptyStatePanel.add(Box.createVerticalGlue());

        body.add(emptyStatePanel, "grow, push");
    }

    private void clearEmptyState() {
        if (emptyStatePanel != null) {
            body.remove(emptyStatePanel);
            emptyStatePanel = null;
        }
        if (emptyStateLabel != null) {
            emptyStateLabel = null;
        }
    }

    public void scrollToBottomNow() {
        scrollToBottom();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp = new javax.swing.JScrollPane();
        body = new javax.swing.JPanel();

        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        body.setBackground(new java.awt.Color(31, 31, 31));

        javax.swing.GroupLayout bodyLayout = new javax.swing.GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 826, Short.MAX_VALUE)
        );
        bodyLayout.setVerticalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 555, Short.MAX_VALUE)
        );

        sp.setViewportView(body);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void scrollToBottom() {
        JScrollBar verticalBar = sp.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }

    private void registerMessage(Chat_Item item, int messageID, String time) {
        if (messageID > 0) {
            messageMap.put(messageID, item);
            itemTimeMap.put(messageID, time);
        }
    }

    private void registerPending(String clientId, Chat_Item item) {
        if (clientId != null) {
            pendingMap.put(clientId, item);
        }
    }

    private void applyReply(Chat_Left item, Model_Receive_Message data) {
        if (data.getReplyUserName() != null && data.getReplyText() != null) {
            item.setReply(data.getReplyUserName(), data.getReplyText());
        }
    }

    private String formatTime(long createdAt) {
        if (createdAt <= 0) {
            return new SimpleDateFormat("hh:mm a").format(new Date());
        }
        return new SimpleDateFormat("hh:mm a").format(new Date(createdAt));
    }

    private String buildFileName(Model_Receive_Message data) {
        if (data.getFileID() == null) {
            return "File";
        }
        String ext = data.getFileExtension() == null ? "" : data.getFileExtension();
        return "File_" + data.getFileID() + ext;
    }

    private String formatFileSize(Long size) {
        if (size == null || size <= 0) {
            return "";
        }
        double value = size;
        String[] units = new String[] { "B", "KB", "MB", "GB" };
        int idx = 0;
        while (value >= 1024 && idx < units.length - 1) {
            value /= 1024;
            idx++;
        }
        return String.format("%.1f %s", value, units[idx]);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private javax.swing.JScrollPane sp;
    private Map<Integer, Chat_Item> messageMap;
    private Map<Integer, String> itemTimeMap;
    private Map<String, Chat_Item> pendingMap;
    private JLabel emptyStateLabel;
    private JPanel emptyStatePanel;
    // End of variables declaration//GEN-END:variables
}
