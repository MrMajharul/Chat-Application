package form;

import com.formdev.flatlaf.FlatClientProperties;
import component.Item_People;
import event.EventMenuLeft;
import event.PublicEvent;
import model.Model_User_Account;
import model.Model_Update_Avatar;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import net.miginfocom.swing.MigLayout;
import swing.RoundedPanel;
import service.Service;
import io.socket.client.Ack;

public class Menu_Left extends javax.swing.JPanel {
    private List<Model_User_Account> userAccount;
    private List<model.Model_Group> groupAccount;
    private Map<Integer, Model_User_Account> userAccountMap;
    private Map<Integer, model.Model_Group> groupAccountMap;

    public Menu_Left() {
        initComponents();
        init();
    }

    private void init() {
        addHoverEffect(menuMessage);
        addHoverEffect(menuGroup);
        menuMessage.setToolTipText("Direct messages");
        menuGroup.setToolTipText("Group chats");
        jButton1.setToolTipText("Log out");
        sp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sp.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "" + "width:5;" + "background:null;" + "trackArc:$ScrollBar.thumbArc;" + "thumbInsets:0,0,0,0;");
        sp.getVerticalScrollBar().setUnitIncrement(10);
        menuList.setLayout(new MigLayout("fillx", "0[fill]0", "0[]0"));
        userAccount = new ArrayList<>();
        groupAccount = new ArrayList<>();
        userAccountMap = new LinkedHashMap<>();
        groupAccountMap = new LinkedHashMap<>();
        PublicEvent.getInstance().addEventMenuLeft(new EventMenuLeft() {
            @Override
            public void newUser(List<Model_User_Account> users) {
                for (Model_User_Account d : users) {
                    userAccountMap.put(d.getUserID(), d);
                }
                userAccount = new ArrayList<>(userAccountMap.values());
                if (menuMessage.isSelected()) {
                    showMessage();
                }
            }

            @Override
            public void userConnect(int userID) {
                for (Model_User_Account u : userAccount) {
                    if (u.getUserID() == userID) {
                        u.setStatus(true);
                        PublicEvent.getInstance().getEventMain().updateUser(u);
                        break;
                    }
                }
                if (menuMessage.isSelected()) {
                    for (Component com : menuList.getComponents()) {
                        Item_People item = (Item_People) com;
                        if (item.getUser().getUserID() == userID) {
                            item.updateStatus();
                            break;
                        }
                    }
                }
            }

            @Override
            public void userDisconnect(int userID) {
                for (Model_User_Account u : userAccount) {
                    if (u.getUserID() == userID) {
                        u.setStatus(false);
                        PublicEvent.getInstance().getEventMain().updateUser(u);
                        break;
                    }
                }
                if (menuMessage.isSelected()) {
                    for (Component com : menuList.getComponents()) {
                        Item_People item = (Item_People) com;
                        if (item.getUser().getUserID() == userID) {
                            item.updateStatus();
                            break;
                        }
                    }
                }
            }

            @Override
            public void updateUser(Model_User_Account user) {
                for (Model_User_Account u : userAccount) {
                    if (u.getUserID() == user.getUserID()) {
                        u.setUserName(user.getUserName());
                        u.setGender(user.getGender());
                        u.setImage(user.getImage());
                        if (menuMessage.isSelected()) {
                            for (Component com : menuList.getComponents()) {
                                Item_People item = (Item_People) com;
                                if (item.getUser().getUserID() == user.getUserID()) {
                                    item.updateUser();
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public List<Model_User_Account> getUserList() {
                return userAccount;
            }

            @Override
            public void newGroup(List<model.Model_Group> groups) {
                for (model.Model_Group g : groups) {
                    groupAccountMap.put(g.getGroupID(), g);
                }
                groupAccount = new ArrayList<>(groupAccountMap.values());
                if (menuGroup.isSelected()) {
                    showGroup();
                }
            }

            @Override
            public List<model.Model_Group> getGroupList() {
                return groupAccount;
            }

            @Override
            public void refreshUnreadBadges() {
                applySelectionState();
                refreshMenuList();
            }
        });
        showMessage();
        initHeaderActions();
        scrollWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollWrapper.setOpaque(false);
        menu.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        menu.setOpaque(false);
    }

    private void initHeaderActions() {
        JPanel header = new JPanel(new MigLayout("ins 12 12 12 12, fillx", "[grow,fill]8[fill]", "[]6[]"));
        header.setBackground(new java.awt.Color(22, 24, 29));
        javax.swing.JLabel title = new javax.swing.JLabel("Chats");
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 17f));
        javax.swing.JLabel subtitle = new javax.swing.JLabel("People and groups");
        subtitle.setForeground(new java.awt.Color(152, 160, 176));
        subtitle.setFont(subtitle.getFont().deriveFont(12f));
        JPanel titleBox = new JPanel(new MigLayout("ins 0, fillx", "[fill]", "[]2[]"));
        titleBox.setOpaque(false);
        titleBox.add(title, "wrap");
        titleBox.add(subtitle);
        JButton profileButton = new JButton("Edit");
        profileButton.setForeground(new java.awt.Color(232, 236, 245));
        profileButton.setBackground(new java.awt.Color(34, 38, 46));
        profileButton.setFocusPainted(false);
        profileButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));
        profileButton.putClientProperty(FlatClientProperties.STYLE, "arc:14");
        profileButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        profileButton.setToolTipText("Edit profile image");
        profileButton.addActionListener(evt -> updateProfileAvatar());
        JButton groupButton = new JButton("+ Group");
        groupButton.setForeground(new java.awt.Color(232, 236, 245));
        groupButton.setBackground(new java.awt.Color(34, 38, 46));
        groupButton.setFocusPainted(false);
        groupButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));
        groupButton.putClientProperty(FlatClientProperties.STYLE, "arc:14");
        groupButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        groupButton.setToolTipText("Create new group");
        groupButton.addActionListener(evt -> createGroup());
        JPanel profileCard = createProfileCard();
        header.add(titleBox, "growx");
        header.add(profileButton);
        header.add(groupButton, "wrap");
        header.add(profileCard, "span, growx");
        scrollWrapper.add(header, java.awt.BorderLayout.NORTH);
    }

    private JPanel createProfileCard() {
        JPanel card = new JPanel(new MigLayout("ins 8 10 8 10, fillx", "[]8[grow,fill]", "[]2[]"));
        card.setBackground(new java.awt.Color(30, 34, 42));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:14");
        swing.ImageAvatar avatar = new swing.ImageAvatar();
        avatar.setBorderSize(0);
        avatar.setPreferredSize(new java.awt.Dimension(32, 32));
        avatar.setImage(new javax.swing.ImageIcon(getClass().getResource("/images/user.png")));
        Model_User_Account me = Service.getInstance().getUser();
        if (me != null && me.getImage() != null && !me.getImage().isEmpty()) {
            try {
                avatar.setImage(new ImageIcon(Base64.getDecoder().decode(me.getImage())));
            } catch (Exception ignore) {
            }
        }
        javax.swing.JLabel name = new javax.swing.JLabel(me != null ? me.getUserName() : "You");
        name.setForeground(new java.awt.Color(235, 240, 250));
        name.putClientProperty(FlatClientProperties.STYLE, "font:+0 bold");
        javax.swing.JLabel status = new javax.swing.JLabel("Online");
        status.setForeground(new java.awt.Color(126, 208, 147));
        status.putClientProperty(FlatClientProperties.STYLE, "font:-1");
        card.add(avatar, "spany 2");
        card.add(name, "wrap");
        card.add(status);
        return card;
    }

    private void createGroup() {
        String groupName = JOptionPane.showInputDialog(this, "Enter group name:", "Create Group", JOptionPane.PLAIN_MESSAGE);
        if (groupName != null && !groupName.trim().isEmpty()) {
            List<Integer> members = new ArrayList<>();
            // For now, let's just add all users as members for simplicity in this demo
            // In a real app, you'd have a selection UI
            for (Model_User_Account u : userAccount) {
                members.add(u.getUserID());
            }
            if (members.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No users available to add to group", "Create Group", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            model.Model_Create_Group data = new model.Model_Create_Group(groupName, service.Service.getInstance().getUser().getUserID(), members);
            service.Service.getInstance().getClient().emit("create_group", data.toJsonObject(), new Ack() {
                @Override
                public void call(Object... os) {
                    if (os.length > 0) {
                        try {
                            boolean success = (boolean) os[0];
                            if (success) {
                                JOptionPane.showMessageDialog(Menu_Left.this, "Group '" + groupName + "' created successfully!", "Create Group", JOptionPane.INFORMATION_MESSAGE);
                                // Request updated group list
                                service.Service.getInstance().getClient().emit("list_group", service.Service.getInstance().getUser().getUserID());
                            } else {
                                JOptionPane.showMessageDialog(Menu_Left.this, "Failed to create group. Please try again.", "Create Group", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(Menu_Left.this, "Error creating group: " + e.getMessage(), "Create Group", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(Menu_Left.this, "No response from server", "Create Group", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
    }

    private void updateProfileAvatar() {
        JFileChooser ch = new JFileChooser();
        ch.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName().toLowerCase();
                return file.isDirectory() || name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".gif");
            }

            @Override
            public String getDescription() {
                return "Image File";
            }
        });
        int option = ch.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = ch.getSelectedFile();
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String base64 = Base64.getEncoder().encodeToString(bytes);
                Model_Update_Avatar update = new Model_Update_Avatar(Service.getInstance().getUser().getUserID(), base64);
                Service.getInstance().getClient().emit("update_avatar", update.toJsonObject(), new Ack() {
                    @Override
                    public void call(Object... os) {
                        // server broadcasts user_update
                    }
                });
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Avatar update failed: " + e.getMessage(), "Profile",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showMessage() {
        menuList.removeAll();
        for (Model_User_Account d : userAccount) {
            Item_People item = new Item_People(d);
            item.setUnreadCount(Service.getInstance().getUnreadUserCount(d.getUserID()));
            item.setPreview(Service.getInstance().getLastUserPreview(d.getUserID()));
            item.setLastTime(Service.getInstance().getLastUserTime(d.getUserID()));
            menuList.add(item, "wrap");
        }
        applySelectionState();
        refreshMenuList();
    }

    private void showGroup() {
        menuList.removeAll();
        for (model.Model_Group g : groupAccount) {
            component.Item_Group item = new component.Item_Group(g);
            item.setUnreadCount(Service.getInstance().getUnreadGroupCount(g.getGroupID()));
            item.setPreview(Service.getInstance().getLastGroupPreview(g.getGroupID()));
            item.setLastTime(Service.getInstance().getLastGroupTime(g.getGroupID()));
            menuList.add(item, "wrap");
        }
        applySelectionState();
        refreshMenuList();
    }

    private void showBox() {
        menuList.removeAll();
        refreshMenuList();
    }

    private void refreshMenuList() {
        menuList.repaint();
        menuList.revalidate();
    }

    private void applySelectionState() {
        Integer selectedUserId = Service.getInstance().getCurrentChatUserId();
        Integer selectedGroupId = Service.getInstance().getCurrentChatGroupId();
        for (Component com : menuList.getComponents()) {
            if (com instanceof Item_People) {
                Item_People item = (Item_People) com;
                item.setSelectedItem(selectedUserId != null && item.getUser().getUserID() == selectedUserId);
                item.setUnreadCount(Service.getInstance().getUnreadUserCount(item.getUser().getUserID()));
            } else if (com instanceof component.Item_Group) {
                component.Item_Group item = (component.Item_Group) com;
                item.setSelectedItem(selectedGroupId != null && item.getGroup().getGroupID() == selectedGroupId);
                item.setUnreadCount(Service.getInstance().getUnreadGroupCount(item.getGroup().getGroupID()));
            }
        }
    }

    private void addHoverEffect(JComponent comp) {
        comp.setOpaque(false);
        comp.setBackground(new java.awt.Color(0, 0, 0, 0));
        comp.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                comp.setBackground(new java.awt.Color(42, 47, 58));
                comp.setOpaque(true);
                comp.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                comp.setBackground(new java.awt.Color(0, 0, 0, 0));
                comp.setOpaque(false);
                comp.repaint();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        menu = new swing.RoundedPanel(20);
        menuMessage = new component.MenuButton();
        menuGroup = new component.MenuButton();
        jButton1 = new javax.swing.JButton();
        sp = new javax.swing.JScrollPane();
        menuList = new javax.swing.JPanel();
        scrollWrapper = new swing.RoundedPanel(20);
        setBackground(new java.awt.Color(16, 18, 22));
        menu.setBackground(new java.awt.Color(22, 24, 29));
        menu.setOpaque(true);
        menu.setLayout(new javax.swing.BoxLayout(menu, javax.swing.BoxLayout.Y_AXIS));
        menuMessage.setIconSelected(new javax.swing.ImageIcon(getClass().getResource("/images/icon-chat-active.png")));
        menuMessage.setIconSimple(new javax.swing.ImageIcon(getClass().getResource("/images/icon-chat-inactive.png")));
        menuMessage.setSelected(true);
        menuMessage.setAlignmentX(CENTER_ALIGNMENT);
        menuMessage.setContentAreaFilled(false);
        menuMessage.setBorderPainted(false);
        menuMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMessageActionPerformed(evt);
            }
        });
        menuGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-group-inactive.png")));
        menuGroup.setIconSelected(new javax.swing.ImageIcon(getClass().getResource("/images/icon-group-active.png")));
        menuGroup.setIconSimple(new javax.swing.ImageIcon(getClass().getResource("/images/icon-group-inactive.png")));
        menuGroup.setAlignmentX(CENTER_ALIGNMENT);
        menuGroup.setContentAreaFilled(false);
        menuGroup.setBorderPainted(false);
        menuGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGroupActionPerformed(evt);
            }
        });
        menu.add(menuMessage);
        menu.add(Box.createVerticalStrut(10));
        menu.add(menuGroup);
        menu.add(Box.createVerticalGlue());
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-logout.png")));
        jButton1.setBackground(new java.awt.Color(31, 31, 31));
        jButton1.setAlignmentX(CENTER_ALIGNMENT);
        jButton1.setContentAreaFilled(false);
        jButton1.setBorderPainted(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        menu.add(jButton1);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuList.setBackground(new java.awt.Color(22, 24, 29));
        menuList.setLayout(new net.miginfocom.swing.MigLayout("fillx, wrap", "[fill]", "[]"));
        sp.setViewportView(menuList);
        scrollWrapper.setBackground(new java.awt.Color(22, 24, 29));
        scrollWrapper.setLayout(new java.awt.BorderLayout());
        scrollWrapper.add(sp, java.awt.BorderLayout.CENTER);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addGap(0, 0, 0)
                        .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, 70,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(scrollWrapper, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                        .addGap(0, 0, 0)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(scrollWrapper, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                                .addComponent(menu))
                        .addGap(0, 0, 0)));
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this, "Log out from this session?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.getWindowAncestor(this).dispose();
            new main.Main().setVisible(true);
        }
    }

    private void menuMessageActionPerformed(java.awt.event.ActionEvent evt) {
        if (!menuMessage.isSelected()) {
            menuMessage.setSelected(true);
            menuGroup.setSelected(false);
            showMessage();
        } else {
            applySelectionState();
            refreshMenuList();
        }
    }

    private void menuGroupActionPerformed(java.awt.event.ActionEvent evt) {
        if (!menuGroup.isSelected()) {
            menuMessage.setSelected(false);
            menuGroup.setSelected(true);
            showGroup();
        } else {
            applySelectionState();
            refreshMenuList();
        }
    }

    private javax.swing.JButton jButton1;
    private swing.RoundedPanel menu;
    private component.MenuButton menuGroup;
    private javax.swing.JPanel menuList;
    private component.MenuButton menuMessage;
    private javax.swing.JScrollPane sp;
    private swing.RoundedPanel scrollWrapper;
}
