package core.ui.component;

import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import util.automaticBindClick;

public class RTextArea extends JTextArea implements MouseListener {
   private static final long serialVersionUID = -6420484506184230135L;
   private JPopupMenu popupMenu;

   public RTextArea() {
      this.init();
   }

   public RTextArea(String string) {
      super(string);
      this.init();
   }

   private void init() {
      this.popupMenu = new JPopupMenu();
      JMenuItem menuItem = new JMenuItem("复制选中");
      menuItem.setActionCommand("copySelectText");
      this.popupMenu.add(menuItem);
      automaticBindClick.bindMenuItemClick(this.popupMenu, (Map)null, this);
      this.addMouseListener(this);
   }

   private void copySelectTextMenuItemClick(ActionEvent actionListener) {
      String selectString = this.getSelectedText();
      if (selectString != null && selectString.trim().length() > 0) {
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(selectString), (ClipboardOwner)null);
         JOptionPane.showMessageDialog(this, "复制成功", "提示", 1);
      } else {
         JOptionPane.showMessageDialog(this, "选中文本是空的", "提示", 2);
      }

   }

   public void mouseClicked(MouseEvent paramMouseEvent) {
   }

   public void mousePressed(MouseEvent paramMouseEvent) {
   }

   public void mouseReleased(MouseEvent paramMouseEvent) {
      if (paramMouseEvent.getButton() == 3) {
         this.popupMenu.show(this, paramMouseEvent.getX(), paramMouseEvent.getY());
      }

   }

   public void mouseEntered(MouseEvent paramMouseEvent) {
   }

   public void mouseExited(MouseEvent paramMouseEvent) {
   }
}
