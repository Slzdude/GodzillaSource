package core.ui.component.dialog;

import javax.swing.*;
import java.awt.*;

public class ImageShowDialog extends JDialog {
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel imageLabel;

    private ImageShowDialog(Frame owner, ImageIcon imageIcon, String title, int width, int height) {
        super(owner, title, true);
        this.imageLabel = new JLabel(imageIcon);
        this.panel.add(this.imageLabel);
        this.add(this.panel);
        this.setSize(width, height);
        this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }

    public static void showImageDiaolog(Frame owner, ImageIcon imageIcon, String title, int width, int height) {
        width += 50;
        height += 50;
        if (title == null || title.trim().length() < 1) {
            title = String.format("image info Width:%s Height:%s", imageIcon.getIconWidth(), imageIcon.getIconHeight());
        }

        new ImageShowDialog(owner, imageIcon, title, width, height);
    }

    public static void showImageDiaolog(Frame owner, ImageIcon imageIcon, String title) {
        showImageDiaolog(owner, imageIcon, title, imageIcon.getIconWidth(), imageIcon.getIconHeight());
    }

    public static void showImageDiaolog(ImageIcon imageIcon, String title) {
        showImageDiaolog(null, imageIcon, title);
    }

    public static void showImageDiaolog(Frame owner, ImageIcon imageIcon) {
        showImageDiaolog(owner, imageIcon, null);
    }

    public static void showImageDiaolog(ImageIcon imageIcon) {
        showImageDiaolog(null, imageIcon);
    }
}
