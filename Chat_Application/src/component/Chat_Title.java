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
import javax.swing.JPanel;

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
        this.user = null;
        lbName.setText(groupName);
        lbStatus.setText("● Group conversation");
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
        layer = new javax.swing.JLayeredPane();
        lbName = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        setBackground(new java.awt.Color(24, 26, 31));
        layer.setLayout(new java.awt.GridLayout(0, 1, 0, 2));
        lbName.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lbName.setForeground(new java.awt.Color(242, 245, 252));
        lbName.setText("Name");
        layer.add(lbName);
        lbStatus.setForeground(new java.awt.Color(88, 200, 125));
        lbStatus.setText("● Online now");
        layer.add(lbStatus);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(layer, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
            .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            .addGap(6, 6, 6)
            .addComponent(layer, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(6, 6, 6))
        );
    }

    private javax.swing.JLayeredPane layer;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbStatus;
}
