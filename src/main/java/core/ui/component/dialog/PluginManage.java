package core.ui.component.dialog;

import core.ApplicationContext;
import core.Db;
import core.ui.MainActivity;
import core.ui.component.DataView;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import util.Log;
import util.automaticBindClick;

public class PluginManage extends JDialog {
   private DataView pluginView;
   private JButton addPluginButton = new JButton("添加");
   private JButton removeButton = new JButton("移除");
   private JButton cancelButton = new JButton("取消");
   private JButton refreshButton = new JButton("刷新");
   private Vector columnVector = new Vector();
   private JSplitPane splitPane = new JSplitPane();

   public PluginManage() {
      super(MainActivity.getFrame(), "PluginManage", true);
      this.columnVector.add("pluginJarFile");
      this.pluginView = new DataView((Vector)null, this.columnVector, -1, -1);
      this.refreshPluginView();
      JPanel bottomPanel = new JPanel();
      bottomPanel.add(this.addPluginButton);
      bottomPanel.add(this.removeButton);
      bottomPanel.add(this.refreshButton);
      bottomPanel.add(this.cancelButton);
      this.splitPane.setOrientation(0);
      this.splitPane.setTopComponent(new JScrollPane(this.pluginView));
      this.splitPane.setBottomComponent(bottomPanel);
      this.splitPane.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            PluginManage.this.splitPane.setDividerLocation(0.85D);
         }
      });
      automaticBindClick.bindJButtonClick(this, this);
      this.add(this.splitPane);
      this.setSize(420, 420);
      this.setLocationRelativeTo(MainActivity.getFrame());
      this.setDefaultCloseOperation(2);
      this.setVisible(true);
   }

   private void refreshPluginView() {
      String[] pluginStrings = Db.getAllPlugin();
      Vector rows = new Vector();

      for(int i = 0; i < pluginStrings.length; ++i) {
         String string = pluginStrings[i];
         Vector rowVector = new Vector();
         rowVector.add(string);
         rows.add(rowVector);
      }

      this.pluginView.AddRows(rows);
      this.pluginView.getModel().fireTableDataChanged();
   }

   private void addPluginButtonClick(ActionEvent actionEvent) {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new FileNameExtensionFilter("*.jar", new String[]{"jar"}));
      chooser.setFileSelectionMode(0);
      chooser.showDialog(new JLabel(), "选择");
      File selectdFile = chooser.getSelectedFile();
      if (selectdFile != null) {
         if (Db.addPlugin(selectdFile.getAbsolutePath()) == 1) {
            ApplicationContext.init();
            JOptionPane.showMessageDialog(this, "添加插件成功", "提示", 1);
         } else {
            JOptionPane.showMessageDialog(this, "添加插件失败", "提示", 2);
         }
      } else {
         Log.log("用户取消选择.....");
      }

      this.refreshPluginView();
   }

   private void removeButtonClick(ActionEvent actionEvent) {
      int rowIndex = this.pluginView.getSelectedRow();
      if (rowIndex != -1) {
         Object selectedItem = this.pluginView.getValueAt(rowIndex, 0);
         if (Db.removePlugin((String)selectedItem) == 1) {
            JOptionPane.showMessageDialog(this, "移除插件成功", "提示", 1);
         } else {
            JOptionPane.showMessageDialog(this, "移除插件失败", "提示", 2);
         }
      } else {
         JOptionPane.showMessageDialog(this, "没有选中插件", "提示", 2);
      }

      this.refreshPluginView();
   }

   private void cancelButtonClick(ActionEvent actionEvent) {
      this.dispose();
   }

   private void refreshButtonClick(ActionEvent actionEvent) {
      this.refreshPluginView();
   }
}
