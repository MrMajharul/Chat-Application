package form;

import model.Model_User_Account;
import net.miginfocom.swing.MigLayout;

public class Home extends javax.swing.JLayeredPane {

    private Chat chat;

    public Home() {
        initComponents();
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx, filly", "0[320!]0[fill, 100%]0", "0[fill]0"));
        setBackground(new java.awt.Color(16, 18, 22));
        this.add(new Menu_Left(), "cell 0 0, grow");
        chat = new Chat();
        this.add(chat, "cell 1 0, grow");
        chat.setVisible(false);
    }

    public void setUser(Model_User_Account user) {
        chat.setUser(user);
        chat.setVisible(true);
    }

    public void setGroup(model.Model_Group group) {
        chat.setGroup(group);
        chat.setVisible(true);
    }

    public void updateUser(Model_User_Account user) {
        chat.updateUser(user);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1007, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 551, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private swing.PictureBox pic;
    private swing.PictureBox pic1;
    private swing.PictureBox pic2;
    // End of variables declaration//GEN-END:variables
}
