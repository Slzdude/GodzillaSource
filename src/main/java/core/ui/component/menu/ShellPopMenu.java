package core.ui.component.menu;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class ShellPopMenu {
    private final JPopupMenu shellmenu;
    private final JMenuItem copy;
    private final JMenuItem paste;
    private final JPanel p;
    private final JTextPane c;
    private final Document shell_doc;
    Clipboard clipboard;
    Transferable contents;
    Transferable tText;
    DataFlavor flavor;

    public ShellPopMenu(JPanel panel, JTextPane console) {
        this.p = panel;
        this.c = console;
        this.shell_doc = console.getDocument();
        this.shellmenu = new JPopupMenu();
        this.copy = new JMenuItem("复制");
        this.paste = new JMenuItem("粘贴");
        this.shellmenu.add(this.copy);
        this.shellmenu.add(this.paste);
        this.p.add(this.shellmenu);
        MenuAction action = new MenuAction();
        this.copy.addActionListener(action);
        this.paste.addActionListener(action);
        console.addMouseListener(new MouseL());
    }

    class MenuAction implements ActionListener {
        MenuAction() {
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == ShellPopMenu.this.copy) {
                String k = ShellPopMenu.this.c.getSelectedText();
                ShellPopMenu.this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                ShellPopMenu.this.tText = new StringSelection(k);
                ShellPopMenu.this.clipboard.setContents(ShellPopMenu.this.tText, null);
            } else if (e.getSource() == ShellPopMenu.this.paste) {
                ShellPopMenu.this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable clipT = ShellPopMenu.this.clipboard.getContents(null);
                if (clipT != null && clipT.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        String pastestr = (String) clipT.getTransferData(DataFlavor.stringFlavor);

                        try {
                            ShellPopMenu.this.shell_doc.insertString(ShellPopMenu.this.shell_doc.getLength(), pastestr, null);
                        } catch (BadLocationException var5) {
                            var5.printStackTrace();
                        }
                    } catch (IOException | UnsupportedFlavorException var6) {
                        var6.printStackTrace();
                    }
                }
            }

        }
    }

    class MouseL implements MouseListener {
        MouseL() {
        }

        public void mouseClicked(MouseEvent e) {
            if (e.isMetaDown()) {
                ShellPopMenu.this.shellmenu.show(ShellPopMenu.this.c, e.getX(), e.getY());
            }

        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }
}
