package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Toast {

    public static void show(JFrame owner, String message) {
        if (owner == null || !owner.isDisplayable()) {
            return;
        }
        JWindow window = new JWindow(owner);
        JLabel label = new JLabel(message);
        label.setOpaque(true);
        label.setBackground(new Color(31, 31, 31));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        window.getContentPane().add(label);
        window.pack();

        Dimension size = window.getSize();
        Point ownerLoc = owner.getLocationOnScreen();
        int x = ownerLoc.x + owner.getWidth() - size.width - 20;
        int y = ownerLoc.y + owner.getHeight() - size.height - 40;
        if (!GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().contains(x, y)) {
            x = Math.max(10, ownerLoc.x + owner.getWidth() - size.width - 20);
            y = Math.max(10, ownerLoc.y + owner.getHeight() - size.height - 40);
        }
        window.setLocation(x, y);
        window.setAlwaysOnTop(true);
        window.setVisible(true);

        Timer timer = new Timer(2500, e -> window.dispose());
        timer.setRepeats(false);
        timer.start();
    }
}
