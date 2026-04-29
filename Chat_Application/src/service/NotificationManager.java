package service;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import swing.Toast;

public class NotificationManager {

    private static TrayIcon trayIcon;
    private static JFrame owner;

    public static void init(JFrame frame) {
        owner = frame;
        if (trayIcon != null) {
            return;
        }
        if (SystemTray.isSupported()) {
            try {
                Image image = new ImageIcon(NotificationManager.class.getResource("/images/icon-bg.png")).getImage();
                trayIcon = new TrayIcon(image, "Chat Application");
                trayIcon.setImageAutoSize(true);
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException e) {
                trayIcon = null;
            }
        }
    }

    public static void notifyMessage(String title, String message, boolean useToast, boolean useSystem, boolean useSound) {
        if (useSound) {
            Toolkit.getDefaultToolkit().beep();
        }
        if (useSystem && trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
        if (useToast && owner != null) {
            SwingUtilities.invokeLater(() -> Toast.show(owner, title + ": " + message));
        }
    }
}
