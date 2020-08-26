package core.ui.component.dialog;

import core.ApplicationContext;
import core.imp.Cryption;
import core.ui.MainActivity;
import core.ui.component.GBC;
import util.Log;
import util.automaticBindClick;
import util.functions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

public class GenerateShellLoder extends JDialog {
    private final JLabel passwordLabel;
    private final JLabel secretKeyLabel;
    private final JLabel cryptionLabel;
    private final JLabel payloadLabel;
    private final JTextField passwordTextField;
    private final JTextField secretKeyTextField;
    private final JComboBox<String> cryptionComboBox;
    private final JComboBox<String> payloadComboBox;
    private final JButton generateButton;
    private final JButton cancelButton;

    public GenerateShellLoder() {
        super(MainActivity.getFrame(), "GenerateShell", true);
        this.setLayout(new GridBagLayout());
        Container c = this.getContentPane();
        GBC gbcLPassword = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
        GBC gbcPassword = (new GBC(1, 0, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcLSecretKey = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
        GBC gbcSecretKey = (new GBC(1, 1, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcLPayload = (new GBC(0, 2)).setInsets(5, -40, 0, 0);
        GBC gbcPayload = (new GBC(1, 2, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcLCryption = (new GBC(0, 3)).setInsets(5, -40, 0, 0);
        GBC gbcCryption = (new GBC(1, 3, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcGenerate = (new GBC(2, 4)).setInsets(5, -40, 0, 0);
        GBC gbcCancel = (new GBC(1, 4, 3, 1)).setInsets(5, 20, 0, 0);
        this.passwordLabel = new JLabel("密码");
        this.secretKeyLabel = new JLabel("密钥");
        this.payloadLabel = new JLabel("有效载荷");
        this.cryptionLabel = new JLabel("加密器");
        this.passwordTextField = new JTextField(16);
        this.secretKeyTextField = new JTextField(16);
        this.payloadComboBox = new JComboBox<>();
        this.cryptionComboBox = new JComboBox<>();
        this.generateButton = new JButton("生成");
        this.cancelButton = new JButton("取消");
        this.passwordTextField.setText("pass");
        this.secretKeyTextField.setText("key");
        c.add(this.passwordLabel, gbcLPassword);
        c.add(this.passwordTextField, gbcPassword);
        c.add(this.secretKeyLabel, gbcLSecretKey);
        c.add(this.secretKeyTextField, gbcSecretKey);
        c.add(this.payloadLabel, gbcLPayload);
        c.add(this.payloadComboBox, gbcPayload);
        c.add(this.cryptionLabel, gbcLCryption);
        c.add(this.cryptionComboBox, gbcCryption);
        c.add(this.generateButton, gbcGenerate);
        c.add(this.cancelButton, gbcCancel);
        this.addToComboBox(this.payloadComboBox, ApplicationContext.getAllPayload());
        this.payloadComboBox.addActionListener(paramActionEvent -> {
            String seleteItemString = (String) GenerateShellLoder.this.payloadComboBox.getSelectedItem();
            GenerateShellLoder.this.cryptionComboBox.removeAllItems();
            GenerateShellLoder.this.addToComboBox(GenerateShellLoder.this.cryptionComboBox, ApplicationContext.getAllCryption(seleteItemString));
        });
        automaticBindClick.bindJButtonClick(this, this);
        functions.fireActionEventByJComboBox(this.payloadComboBox);
        this.setSize(430, 230);
        this.setLocationRelativeTo(MainActivity.getFrame());
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void generateButtonClick(ActionEvent actionEvent) {
        String password = this.passwordTextField.getText();
        String secretKey = this.secretKeyTextField.getText();
        if (password == null || secretKey == null || password.trim().length() <= 0 || secretKey.trim().length() <= 0) {
            JOptionPane.showMessageDialog(this, "password 或\t secretKey  是空的!", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (this.payloadComboBox.getSelectedItem() == null || this.cryptionComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "payload 或  cryption 没有选中!", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedPayload = (String) this.payloadComboBox.getSelectedItem();
        String selectedCryption = (String) this.cryptionComboBox.getSelectedItem();
        Cryption cryption = ApplicationContext.getCryption(selectedPayload, selectedCryption);
        byte[] data = cryption.generate(password, secretKey);
        if (data == null) {
            JOptionPane.showMessageDialog(this, "加密器在生成时返回空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showDialog(new JLabel(), "选择");
        File selectedFile = chooser.getSelectedFile();
        if (selectedFile != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(selectedFile);
                fileOutputStream.write(data);
                fileOutputStream.close();
                JOptionPane.showMessageDialog(this, "success! save file to -> " + selectedFile.getAbsolutePath(), "提示", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } catch (Exception var11) {
                Log.error(var11);
            }
        } else {
            Log.log("用户取消选择....");
        }
    }

    private void cancelButtonClick(ActionEvent actionEvent) {
        this.dispose();
    }

    private void addToComboBox(JComboBox<String> comboBox, String[] data) {
        for (String datum : data) {
            comboBox.addItem(datum);
        }

    }
}
