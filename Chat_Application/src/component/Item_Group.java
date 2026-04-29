package component;

import com.formdev.flatlaf.FlatClientProperties;
import event.PublicEvent;
import model.Model_Group;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

public class Item_Group extends swing.RoundedPanel {

    public Model_Group getGroup() {
        return group;
    }

    private boolean mouseOver;
    private boolean selectedItem;
    private int unreadCount;
    private final Model_Group group;

    public Item_Group(Model_Group group) {
        this.group = group;
        initComponents();
        lb.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+1;");
        lb.setText(group.getGroupName());
        lbStatus.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:-1 italic;");
        lbStatus.setText("Group Chat");
        init();
    }

    private void init() {
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                mouseOver = true;
                applyStateColor();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                mouseOver = false;
                applyStateColor();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (mouseOver) {
                    if (getParent() != null) {
                        for (java.awt.Component c : getParent().getComponents()) {
                            if (c instanceof Item_Group) {
                                ((Item_Group) c).setSelectedItem(false);
                            }
                        }
                    }
                    setSelectedItem(true);
                    PublicEvent.getInstance().getEventMain().selectGroup(group);
                }
            }
        });
        applyStateColor();
    }

    public void setSelectedItem(boolean selectedItem) {
        this.selectedItem = selectedItem;
        applyStateColor();
        repaint();
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = Math.max(0, unreadCount);
        if (this.unreadCount > 0) {
            unreadBadge.setText(this.unreadCount > 99 ? "99+" : String.valueOf(this.unreadCount));
            unreadBadge.setVisible(true);
        } else {
            unreadBadge.setVisible(false);
        }
    }

    public void setPreview(String preview) {
        lbStatus.setText(compactPreview(preview));
    }

    public void setLastTime(String time) {
        lbTime.setText(time == null ? "" : time);
    }

    private String compactPreview(String preview) {
        if (preview == null || preview.trim().isEmpty()) {
            return "No messages yet";
        }
        String value = preview.trim();
        return value.length() > 24 ? value.substring(0, 24) + "..." : value;
    }

    private void applyStateColor() {
        if (selectedItem) {
            setBackground(new Color(35, 69, 108));
            lb.setForeground(new Color(245, 248, 255));
            lbStatus.setForeground(new Color(212, 224, 245));
        } else if (mouseOver) {
            setBackground(new Color(38, 43, 53));
            lb.setForeground(new Color(255, 255, 255));
            lbStatus.setForeground(new Color(191, 198, 214));
        } else {
            setBackground(new Color(22, 24, 29));
            lb.setForeground(new Color(239, 242, 249));
            lbStatus.setForeground(new Color(163, 170, 186));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (selectedItem) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(90, 173, 255));
            g2.fillRoundRect(0, 8, 4, Math.max(16, getHeight() - 16), 4, 4);
            g2.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        imageAvatar1 = new swing.ImageAvatar();
        lb = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        unreadBadge = new javax.swing.JLabel();
        lbTime = new javax.swing.JLabel();

        setBackground(new java.awt.Color(22, 24, 29));

        imageAvatar1.setBorderSize(0);
        imageAvatar1.setImage(new javax.swing.ImageIcon(getClass().getResource("/images/group.png"))); // NOI18N

        javax.swing.GroupLayout imageAvatar1Layout = new javax.swing.GroupLayout(imageAvatar1);
        imageAvatar1.setLayout(imageAvatar1Layout);
        imageAvatar1Layout.setHorizontalGroup(
            imageAvatar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        imageAvatar1Layout.setVerticalGroup(
            imageAvatar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        lb.setForeground(new java.awt.Color(239, 242, 249));
        lb.setText("Group Name");

        lbStatus.setForeground(new java.awt.Color(163, 170, 186));
        lbStatus.setText("Group Chat");

        lbTime.setForeground(new java.awt.Color(138, 146, 163));
        lbTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbTime.setText("");
        lbTime.putClientProperty(FlatClientProperties.STYLE, "font:-2");

        unreadBadge.setBackground(new java.awt.Color(83, 153, 247));
        unreadBadge.setForeground(new java.awt.Color(255, 255, 255));
        unreadBadge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        unreadBadge.setText("1");
        unreadBadge.setOpaque(true);
        unreadBadge.setVisible(false);
        unreadBadge.putClientProperty(FlatClientProperties.STYLE, "arc:999;font:-2 bold");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(imageAvatar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbTime, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(unreadBadge, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb)
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(unreadBadge, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(imageAvatar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );
    }

    private swing.ImageAvatar imageAvatar1;
    private javax.swing.JLabel lb;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTime;
    private javax.swing.JLabel unreadBadge;
}
