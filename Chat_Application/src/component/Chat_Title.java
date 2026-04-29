package component;

import com.formdev.flatlaf.FlatClientProperties;
import model.Model_User_Account;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.util.Base64;

public class Chat_Title extends JPanel {
    public Model_User_Account getUser() {
        return user;
    }

    private Model_User_Account user;

    public Chat_Title() {
        initComponents();
        setOpaque(false);
        lbName.putClientProperty(FlatClientProperties.STYLE, "font:+3 bold;");
        lbStatus.putClientProperty(FlatClientProperties.STYLE, "font:+0");
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    public void setUserName(Model_User_Account user) {
        this.user = user;
        lbName.setText(user.getUserName());
        avatar.setImage(new ImageIcon(getClass().getResource("/images/user.png")));
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            try {
                avatar.setImage(new ImageIcon(Base64.getDecoder().decode(user.getImage())));
            } catch (Exception ignore) {
            }
        }
        if (user.isStatus()) {
            statusActive();
        } else {
            setStatusText("Offline");
        }
    }

    public void updateUser(Model_User_Account user) {
        if (this.user == user) {
            lbName.setText(user.getUserName());
            if (user.isStatus()) {
                statusActive();
            } else {
                setStatusText("Offline");
            }
        }
    }

    private void statusActive() {
        lbStatus.setText("● Online now");
        lbStatus.setForeground(new java.awt.Color(88, 200, 125));
    }

    private void setStatusText(String text) {
        lbStatus.setText(text);
        lbStatus.setForeground(new Color(170, 178, 194));
    }

    public void setGroupName(String groupName) {
        setGroupName(groupName, "Group conversation");
    }

    public void setGroupName(String groupName, String info) {
        this.user = null;
        lbName.setText(groupName);
        avatar.setImage(new ImageIcon(getClass().getResource("/images/group.png")));
        lbStatus.setText("● " + (info == null || info.trim().isEmpty() ? "Group conversation" : info));
        lbStatus.setForeground(new java.awt.Color(110, 178, 255));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        Shape topRounded = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 20, 20, 20);
        g2.fill(topRounded);
        g2.dispose();
        super.paintComponent(g);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        avatar = new swing.ImageAvatar();
        layer = new javax.swing.JLayeredPane();
        lbName = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        actions = new javax.swing.JPanel();
        btnCall = new javax.swing.JButton();
        btnVideo = new javax.swing.JButton();
        btnInfo = new javax.swing.JButton();
        setBackground(new java.awt.Color(24, 26, 31));
        avatar.setBorderSize(0);
        avatar.setImage(new javax.swing.ImageIcon(getClass().getResource("/images/user.png"))); // NOI18N
        javax.swing.GroupLayout avatarLayout = new javax.swing.GroupLayout(avatar);
        avatar.setLayout(avatarLayout);
        avatarLayout.setHorizontalGroup(
            avatarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );
        avatarLayout.setVerticalGroup(
            avatarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );
        layer.setLayout(new java.awt.GridLayout(0, 1, 0, 2));
        lbName.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lbName.setForeground(new java.awt.Color(242, 245, 252));
        lbName.setText("Name");
        layer.add(lbName);
        lbStatus.setForeground(new java.awt.Color(88, 200, 125));
        lbStatus.setText("● Online now");
        layer.add(lbStatus);
        actions.setOpaque(false);
        actions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        configureActionButton(btnCall, "Call");
        configureActionButton(btnVideo, "Video");
        configureActionButton(btnInfo, "Info");
        actions.add(btnCall);
        actions.add(btnVideo);
        actions.add(btnInfo);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(avatar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(10, 10, 10)
            .addComponent(layer, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
            .addGap(10, 10, 10)
            .addComponent(actions, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            .addGap(6, 6, 6)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(avatar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(layer, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(actions, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(6, 6, 6))
        );
    }

    private void configureActionButton(JButton button, String text) {
        button.setText(text);
        button.setToolTipText(text);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setBackground(new Color(34, 38, 46));
        button.setForeground(new Color(228, 234, 246));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "arc:10;focusWidth:0");
    }

    private javax.swing.JPanel actions;
    private swing.ImageAvatar avatar;
    private javax.swing.JButton btnCall;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnVideo;
    private javax.swing.JLayeredPane layer;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbStatus;
}
