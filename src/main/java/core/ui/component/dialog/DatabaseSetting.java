package core.ui.component.dialog;

import core.shell.ShellEntity;
import core.ui.component.GBC;
import core.ui.component.model.DbInfo;
import util.Log;
import util.automaticBindClick;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DatabaseSetting extends JDialog {
    private final JComboBox<String> dbTypeComboBox;
    private final JTextField dbHostTextField;
    private final JTextField dbPortTextField;
    private final JTextField dbUserNameTextField;
    private final JTextField dbPasswordTextField;
    private final JLabel dbTypeLabel;
    private final JLabel dbHostLabel;
    private final JLabel dbPortLabel;
    private final JLabel dbUserNameLabel;
    private final JLabel dbPasswordLabel;
    private final JButton updateButton;
    private final Dimension TextFieldDim = new Dimension(200, 23);
    private final Dimension labelDim = new Dimension(150, 23);
    private final DbInfo dbInfo;

    public DatabaseSetting(ShellEntity shellEntity, DbInfo dbInfo) {
        super(shellEntity.getFrame(), "DbInfo Setting", true);
        String[] databaseTypeArray = shellEntity.getPayloadModel().getAllDatabaseType();
        Container c = this.getContentPane();
        this.setLayout(new GridBagLayout());
        this.dbInfo = dbInfo;
        GBC gbcLDbType = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
        GBC gbcDbType = (new GBC(1, 0, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcLDbHost = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
        GBC gbcDbHost = (new GBC(1, 1, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcLDbPort = (new GBC(0, 2)).setInsets(5, -40, 0, 0);
        GBC gbcDbPort = (new GBC(1, 2, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcLDbUserName = (new GBC(0, 3)).setInsets(5, -40, 0, 0);
        GBC gbcDbUserName = (new GBC(1, 3, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcLDbPassword = (new GBC(0, 4)).setInsets(5, -40, 0, 0);
        GBC gbcDbPassword = (new GBC(1, 4, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcUpdate = (new GBC(1, 5, 4, 1)).setInsets(5, 20, 0, 0);
        this.updateButton = new JButton("Update Db Info");
        this.dbTypeLabel = new JLabel("数据库类型");
        this.dbHostLabel = new JLabel("数据库主机");
        this.dbPortLabel = new JLabel("数据库端口");
        this.dbUserNameLabel = new JLabel("数据库用户名");
        this.dbPasswordLabel = new JLabel("数据库密码");
        this.dbTypeLabel.setPreferredSize(this.labelDim);
        this.dbHostLabel.setPreferredSize(this.labelDim);
        this.dbPortLabel.setPreferredSize(this.labelDim);
        this.dbUserNameLabel.setPreferredSize(this.labelDim);
        this.dbPasswordLabel.setPreferredSize(this.labelDim);
        this.dbHostTextField = new JTextField(dbInfo.getDbHost());
        this.dbPortTextField = new JTextField(Integer.toString(dbInfo.getDbPort()));
        this.dbUserNameTextField = new JTextField(dbInfo.getDbUserName());
        this.dbPasswordTextField = new JTextField(dbInfo.getDbPassword());
        this.dbTypeComboBox = new JComboBox<>(databaseTypeArray);
        this.dbTypeComboBox.setEditable(false);
        this.dbTypeComboBox.setSelectedItem(dbInfo.getDbType());
        this.dbTypeComboBox.setPreferredSize(this.TextFieldDim);
        this.dbHostTextField.setPreferredSize(this.TextFieldDim);
        this.dbPortTextField.setPreferredSize(this.TextFieldDim);
        this.dbUserNameTextField.setPreferredSize(this.TextFieldDim);
        this.dbPasswordTextField.setPreferredSize(this.TextFieldDim);
        this.updateButton.setPreferredSize(this.TextFieldDim);
        c.add(this.dbTypeLabel, gbcLDbType);
        c.add(this.dbTypeComboBox, gbcDbType);
        c.add(this.dbHostLabel, gbcLDbHost);
        c.add(this.dbHostTextField, gbcDbHost);
        c.add(this.dbPortLabel, gbcLDbPort);
        c.add(this.dbPortTextField, gbcDbPort);
        c.add(this.dbUserNameLabel, gbcLDbUserName);
        c.add(this.dbUserNameTextField, gbcDbUserName);
        c.add(this.dbPasswordLabel, gbcLDbPassword);
        c.add(this.dbPasswordTextField, gbcDbPassword);
        c.add(this.updateButton, gbcUpdate);
        automaticBindClick.bindJButtonClick(this, this);
        this.setSize(460, 270);
        this.setLocationRelativeTo(shellEntity.getFrame());
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void updateButtonClick(ActionEvent actionEvent) {
        try {
            String dbType = (String) this.dbTypeComboBox.getSelectedItem();
            if (dbType.trim().length() > 0) {
                this.dbInfo.setDbHost(this.dbHostTextField.getText());
                this.dbInfo.setDbPort(Integer.parseInt(this.dbPortTextField.getText()));
                this.dbInfo.setDbUserName(this.dbUserNameTextField.getText());
                this.dbInfo.setDbPassword(this.dbPasswordTextField.getText());
                this.dbInfo.setDbType(dbType);
                JOptionPane.showMessageDialog(this, "success", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "no selected DbType", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception var3) {
            JOptionPane.showMessageDialog(this, var3.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
            Log.error(var3);
        }

        this.dispose();
    }
}
