package core.ui.component;

import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.menu.ShellPopMenu;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import util.Log;

public class ShellExecCommandPanel extends JPanel {
   private static final long serialVersionUID = 1L;
   private int command_start;
   private int command_stop;
   private JToolBar bar;
   private JLabel status;
   private JTextPane console;
   private JScrollPane console_scroll;
   private Document shell_doc;
   private ArrayList last_commands = new ArrayList();
   private int num = 1;
   private Payload shell;
   private String currentDir;
   private String currentUser;
   private String fileRoot;
   private String osInfo;
   private ShellPopMenu shellPopMenu;

   public ShellExecCommandPanel(ShellEntity shellEntity) {
      this.shell = shellEntity.getPayloadModel();
      this.bar = new JToolBar();
      this.status = new JLabel("完成");
      this.bar.setFloatable(false);
      this.console = new JTextPane();
      this.console_scroll = new JScrollPane(this.console);
      this.shell_doc = this.console.getDocument();
      this.shellPopMenu = new ShellPopMenu(this, this.console);
      this.currentDir = this.shell.currentDir();
      this.currentUser = this.shell.currentUserName();
      this.fileRoot = Arrays.toString(shellEntity.getPayloadModel().listFileRoot());
      this.osInfo = this.shell.getOsInfo();
      this.status.setText("正在连接...请稍等");
      Thread thread_getpath = new Thread(new Runnable() {
         public void run() {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  try {
                     ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.shell_doc.getLength(), String.format("currentDir:%s\nfileRoot:%s\ncurrentUser:%s\nosInfo:%s\n", ShellExecCommandPanel.this.currentDir, ShellExecCommandPanel.this.fileRoot, ShellExecCommandPanel.this.currentUser, ShellExecCommandPanel.this.osInfo), (AttributeSet)null);
                     ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.shell_doc.getLength(), "\n" + ShellExecCommandPanel.this.currentDir + " >", (AttributeSet)null);
                  } catch (BadLocationException var2) {
                     Log.error((Exception)var2);
                  }

                  ShellExecCommandPanel.this.command_start = ShellExecCommandPanel.this.shell_doc.getLength();
                  ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
                  ShellExecCommandPanel.this.status.setText("完成");
               }
            });
         }
      });
      thread_getpath.start();
      this.setLayout(new GridBagLayout());
      GBC gbcinfo = (new GBC(0, 0, 6, 1)).setFill(2).setWeight(100.0D, 0.0D);
      GBC gbcconsole = (new GBC(0, 1, 6, 1)).setFill(1).setWeight(0.0D, 10.0D);
      GBC gbcbar = (new GBC(0, 2, 6, 1)).setFill(2).setWeight(100.0D, 0.0D);
      textareaFocus f_listener = new textareaFocus((textareaFocus)null);
      this.addFocusListener(f_listener);
      textareaKey key_listener = new textareaKey((textareaKey)null);
      this.console.addKeyListener(key_listener);
      this.bar.add(this.status);
      this.add(this.bar, gbcinfo);
      this.add(this.console_scroll, gbcconsole);
      this.add(this.bar, gbcbar);
      this.console.setCaretPosition(this.shell_doc.getLength());
      Color bgColor = Color.BLACK;
      UIDefaults defaults = new UIDefaults();
      defaults.put("TextPane[Enabled].backgroundPainter", bgColor);
      this.console.putClientProperty("Nimbus.Overrides", defaults);
      this.console.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
      this.console.setBackground(bgColor);
      this.console.setForeground(Color.green);
      this.console.setBackground(Color.black);
      this.console.setCaretColor(Color.white);
      this.command_start = this.shell_doc.getLength();
   }

   public void execute(String command) {
      String result = "\n";

      try {
         if (command.trim().length() > 0) {
            result = result + this.shell.execCommand(command);
         } else {
            result = result + "NULL";
         }

         this.shell_doc.insertString(this.shell_doc.getLength(), result, (AttributeSet)null);
         this.shell_doc.insertString(this.shell_doc.getLength(), "\n" + this.currentDir + " >", (AttributeSet)null);
         this.command_start = this.shell_doc.getLength();
         this.console.setCaretPosition(this.shell_doc.getLength());
         this.status.setText("完成");
      } catch (Exception var6) {
         try {
            this.shell_doc.insertString(this.shell_doc.getLength(), "\nNull", (AttributeSet)null);
            this.shell_doc.insertString(this.shell_doc.getLength(), "\n" + this.currentDir + " >", (AttributeSet)null);
            this.command_start = this.shell_doc.getLength();
            this.console.setCaretPosition(this.shell_doc.getLength());
         } catch (Exception var5) {
            Log.error(var5);
         }
      }

   }

   public String key_up_action() {
      --this.num;
      String last_command = null;
      if (this.num >= 0 && !this.last_commands.isEmpty()) {
         last_command = (String)this.last_commands.get(this.num);
         last_command = last_command.replace("\n", "").replace("\r", "");
         return last_command;
      } else {
         return "";
      }
   }

   public String key_down_action() {
      ++this.num;
      String last_command = null;
      if (this.num < this.last_commands.size() && this.num >= 0) {
         last_command = (String)this.last_commands.get(this.num);
         last_command = last_command.replace("\n", "").replace("\r", "");
         return last_command;
      } else if (this.num < 0) {
         this.num = 0;
         return "";
      } else {
         this.num = this.last_commands.size();
         return "";
      }
   }

   public static String toHexString(String s) {
      String str = "";

      for(int i = 0; i < s.length(); ++i) {
         int ch = s.charAt(i);
         String s4 = Integer.toHexString(ch);
         str = str + s4;
      }

      return str;
   }

   private class textareaFocus extends FocusAdapter {
      private textareaFocus() {
      }

      public void focusGained(FocusEvent e) {
         ShellExecCommandPanel.this.console.requestFocus();
         ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
      }
   }

   private class textareaKey extends KeyAdapter {
      private textareaKey() {
      }

      public void keyPressed(KeyEvent arg0) {
         if (ShellExecCommandPanel.this.shell_doc.getLength() <= ShellExecCommandPanel.this.command_start && !arg0.isControlDown() && arg0.getKeyCode() == 8) {
            try {
               String t = ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.console.getCaretPosition() - 1, 1);
               ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.console.getCaretPosition(), t, (AttributeSet)null);
            } catch (Exception var3) {
            }
         }

         if ((ShellExecCommandPanel.this.console.getCaretPosition() < ShellExecCommandPanel.this.command_start || ShellExecCommandPanel.this.console.getSelectionStart() < ShellExecCommandPanel.this.command_start || ShellExecCommandPanel.this.console.getSelectionEnd() < ShellExecCommandPanel.this.command_start) && !arg0.isControlDown()) {
            ShellExecCommandPanel.this.console.setEditable(false);
            ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
         } else if (arg0.isControlDown() && ShellExecCommandPanel.this.console.getCaretPosition() < ShellExecCommandPanel.this.command_start) {
            ShellExecCommandPanel.this.console.setEditable(false);
         } else {
            ShellExecCommandPanel.this.console.setEditable(true);
         }

         if (arg0.getKeyCode() == 10) {
            ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.shell_doc.getLength());
         }

      }

      public synchronized void keyReleased(KeyEvent arg0) {
         ShellExecCommandPanel.this.command_stop = ShellExecCommandPanel.this.shell_doc.getLength();
         if (arg0.getKeyCode() == 10) {
            String tmp_cmd = null;

            try {
               tmp_cmd = ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.command_stop - ShellExecCommandPanel.this.command_start);
               tmp_cmd = tmp_cmd.replace("\n", "").replace("\r", "");
               if (!tmp_cmd.equals("cls") && !tmp_cmd.equals("clear")) {
                  ShellExecCommandPanel.this.status.setText("正在执行...请稍等");

                  try {
                     ShellExecCommandPanel.this.execute(ShellExecCommandPanel.this.shell_doc.getText(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.command_stop - ShellExecCommandPanel.this.command_start));
                  } catch (Exception var6) {
                     ShellExecCommandPanel.this.status.setText("执行失败");
                     ShellExecCommandPanel.this.console.setEditable(true);
                  }
               } else {
                  ShellExecCommandPanel.this.shell_doc.remove(0, ShellExecCommandPanel.this.shell_doc.getLength());
                  ShellExecCommandPanel.this.shell_doc.insertString(0, "\n" + ShellExecCommandPanel.this.currentDir + " >", (AttributeSet)null);
                  ShellExecCommandPanel.this.command_start = ShellExecCommandPanel.this.shell_doc.getLength();
               }

               if (tmp_cmd.trim().length() > 0) {
                  ShellExecCommandPanel.this.last_commands.add(tmp_cmd);
               }

               ShellExecCommandPanel.this.num = ShellExecCommandPanel.this.last_commands.size();
            } catch (BadLocationException var7) {
               var7.printStackTrace();
            }
         }

         if (arg0.getKeyCode() == 38) {
            ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.command_start);

            try {
               ShellExecCommandPanel.this.shell_doc.remove(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.shell_doc.getLength() - ShellExecCommandPanel.this.command_start);
               ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.key_up_action(), (AttributeSet)null);
            } catch (BadLocationException var5) {
               var5.printStackTrace();
            }
         }

         if (arg0.getKeyCode() == 40) {
            ShellExecCommandPanel.this.console.setCaretPosition(ShellExecCommandPanel.this.command_start);

            try {
               ShellExecCommandPanel.this.shell_doc.remove(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.shell_doc.getLength() - ShellExecCommandPanel.this.command_start);
               ShellExecCommandPanel.this.shell_doc.insertString(ShellExecCommandPanel.this.command_start, ShellExecCommandPanel.this.key_down_action(), (AttributeSet)null);
            } catch (BadLocationException var4) {
               var4.printStackTrace();
            }
         }

      }
   }
}
