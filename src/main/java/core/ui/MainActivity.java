package core.ui;

import core.ApplicationContext;
import core.Db;
import core.ui.component.DataView;
import core.ui.component.dialog.AppSeting;
import core.ui.component.dialog.GenerateShellLoder;
import core.ui.component.dialog.PluginManage;
import core.ui.component.dialog.ShellSetting;
import util.automaticBindClick;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class MainActivity {
    private static JFrame mainActivityFrame;
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu targetMenu;
    private JMenu aboutMenu;
    private JMenu attackMenu;
    private JMenu configMenu;
    private DataView shellView;
    private JScrollPane shellViewScrollPane;
    private JPopupMenu shellViewPopupMenu;
    private Vector<String> columnVector;

    public MainActivity() {
        ApplicationContext.init();
        this.initVariable();
    }

    public static JFrame getFrame() {
        return mainActivityFrame;
    }

    public static void main(String[] args) {
        MainActivity activity = new MainActivity();
        mainActivityFrame = activity.getJFrame();
    }

    private void initVariable() {
        this.frame = new JFrame();
        this.frame.setTitle(String.format("哥斯拉\t V%s    by: BeichenDream", "1.00"));
        this.frame.setLayout(new BorderLayout(1, 1));
        Vector<Vector<String>> rows = Db.getAllShell();
        this.columnVector = rows.get(0);
        rows.remove(0);
        this.shellView = new DataView(null, this.columnVector, -1, -1);
        this.shellView.AddRows(rows);
        this.frame.add(this.shellViewScrollPane = new JScrollPane(this.shellView));
        this.menuBar = new JMenuBar();
        this.targetMenu = new JMenu("目标");
        JMenuItem addShellMenuItem = new JMenuItem("添加");
        addShellMenuItem.setActionCommand("addShell");
        this.targetMenu.add(addShellMenuItem);
        this.attackMenu = new JMenu("管理");
        JMenuItem generateShellMenuItem = new JMenuItem("生成");
        generateShellMenuItem.setActionCommand("generateShell");
        this.attackMenu.add(generateShellMenuItem);
        this.configMenu = new JMenu("配置");
        JMenuItem pluginConfigMenuItem = new JMenuItem("插件配置");
        pluginConfigMenuItem.setActionCommand("pluginConfig");
        JMenuItem appConfigMenuItem = new JMenuItem("程序配置");
        appConfigMenuItem.setActionCommand("appConfig");
        this.configMenu.add(appConfigMenuItem);
        this.configMenu.add(pluginConfigMenuItem);
        this.aboutMenu = new JMenu("关于");
        JMenuItem aboutMenuItem = new JMenuItem("关于");
        aboutMenuItem.setActionCommand("about");
        this.aboutMenu.add(aboutMenuItem);
        automaticBindClick.bindMenuItemClick(this.targetMenu, null, this);
        automaticBindClick.bindMenuItemClick(this.attackMenu, null, this);
        automaticBindClick.bindMenuItemClick(this.configMenu, null, this);
        automaticBindClick.bindMenuItemClick(this.aboutMenu, null, this);
        this.menuBar.add(this.targetMenu);
        this.menuBar.add(this.attackMenu);
        this.menuBar.add(this.configMenu);
        this.menuBar.add(this.aboutMenu);
        this.frame.setJMenuBar(this.menuBar);
        this.shellViewPopupMenu = new JPopupMenu();
        JMenuItem copyselectItem = new JMenuItem("复制选中");
        copyselectItem.setActionCommand("copyShellViewSelected");
        JMenuItem interactMenuItem = new JMenuItem("进入");
        interactMenuItem.setActionCommand("interact");
        JMenuItem removeShell = new JMenuItem("移除");
        removeShell.setActionCommand("removeShell");
        JMenuItem editShell = new JMenuItem("编辑");
        editShell.setActionCommand("editShell");
        JMenuItem refreshShell = new JMenuItem("刷新");
        refreshShell.setActionCommand("refreshShellView");
        this.shellViewPopupMenu.add(interactMenuItem);
        this.shellViewPopupMenu.add(copyselectItem);
        this.shellViewPopupMenu.add(removeShell);
        this.shellViewPopupMenu.add(editShell);
        this.shellViewPopupMenu.add(refreshShell);
        this.shellView.setRightClickMenu(this.shellViewPopupMenu);
        automaticBindClick.bindMenuItemClick(this.shellViewPopupMenu, null, this);
        this.frame.setSize(1500, 600);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void addShellMenuItemClick(ActionEvent e) {
        new ShellSetting(null);
        this.refreshShellView();
    }

    private void generateShellMenuItemClick(ActionEvent e) {
        new GenerateShellLoder();
    }

    private void pluginConfigMenuItemClick(ActionEvent e) {
        new PluginManage();
    }

    private void appConfigMenuItemClick(ActionEvent e) {
        new AppSeting();
    }

    private void aboutMenuItemClick(ActionEvent e) {
        JOptionPane.showMessageDialog(getFrame(), "由BeichenDream强力驱动\n邮箱:beichendream@gmail.com", "About", JOptionPane.PLAIN_MESSAGE);
        // functions.openBrowseUrl("https://github.com/BeichenDream/Godzilla");
    }

    private void copyShellViewSelectedMenuItemClick(ActionEvent e) {
        int columnIndex = this.shellView.getSelectedColumn();
        if (columnIndex != -1) {
            Object o = this.shellView.getValueAt(this.shellView.getSelectedRow(), this.shellView.getSelectedColumn());
            if (o != null) {
                String value = (String) o;
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), null);
                JOptionPane.showMessageDialog(this.shellView, "复制成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this.shellView, "选中列是空的", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this.shellView, "未选中列", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void removeShellMenuItemClick(ActionEvent e) {
        Object o = this.shellView.getValueAt(this.shellView.getSelectedRow(), 0);
        if (o != null && o.getClass().isAssignableFrom(String.class)) {
            String shellId = (String) o;
            if (Db.removeShell(shellId) > 0) {
                JOptionPane.showMessageDialog(this.shellView, "删除成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                this.refreshShellView();
            } else {
                JOptionPane.showMessageDialog(this.shellView, "删除失败", "提示", JOptionPane.WARNING_MESSAGE);
            }
        }

    }

    private void editShellMenuItemClick(ActionEvent e) {
        Object o = this.shellView.getValueAt(this.shellView.getSelectedRow(), 0);
        if (o != null && o.getClass().isAssignableFrom(String.class)) {
            new ShellSetting((String) o);
            this.refreshShellView();
        }

    }

    private void interactMenuItemClick(ActionEvent e) {
        String shellId = (String) this.shellView.getValueAt(this.shellView.getSelectedRow(), 0);
        new ShellManage(shellId);
    }

    private void refreshShellView() {
        Vector rowsVector = Db.getAllShell();
        rowsVector.remove(0);
        this.shellView.AddRows(rowsVector);
        this.shellView.getModel().fireTableDataChanged();
    }

    private void refreshShellViewMenuItemClick(ActionEvent e) {
        this.refreshShellView();
    }

    public JFrame getJFrame() {
        return this.frame;
    }
}
