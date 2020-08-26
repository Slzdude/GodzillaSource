package core.ui.component;

import core.ui.imp.ActionDblClick;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class DataTree extends JTree {
    private final DefaultMutableTreeNode rootNode;
    private RightClickEvent rightClickEvent;
    private ImageIcon leafIcon;

    public DataTree(String fileString, DefaultMutableTreeNode root_Node) {
        super(root_Node);
        this.rootNode = root_Node;
        this.initJTree();
    }

    private void initJTree() {
        this.rightClickEvent = new RightClickEvent(this);
        this.addMouseListener(this.rightClickEvent);
        this.getSelectionModel().setSelectionMode(1);
    }

    public void setActionDbclick(ActionDblClick actionDblClick) {
        this.rightClickEvent.setActionDblClick(actionDblClick);
    }

    public void setChildPopupMenu(JPopupMenu popupMenu) {
        this.rightClickEvent.setChildPopupMenu(popupMenu);
    }

    public void DeleteNote(String fileString) {
        DefaultMutableTreeNode defaultMutableTreeNode = this.rootNode;
        String[] paths = this.parseFile(fileString);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) defaultMutableTreeNode.getPath()[0];
        DefaultMutableTreeNode lasTreeNode = null;

        for (int i = 0; i < paths.length; ++i) {
            node = this.FindTreeNote(node, paths[i]);
            if (node == null) {
                return;
            }

            if (i == paths.length - 2) {
                lasTreeNode = node;
            }
        }

        if (lasTreeNode != null) {
            lasTreeNode.remove(lasTreeNode);
        }

    }

    public void setLeafIcon(ImageIcon imageIcon) {
        this.leafIcon = imageIcon;
    }

    public void updateUI() {
        super.updateUI();
        if (this.leafIcon != null) {
            DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer) this.getCellRenderer();
            cellRenderer.setLeafIcon(cellRenderer.getClosedIcon());
        }

    }

    public void removeAll() {
        super.removeAll();
        this.rootNode.removeAllChildren();
        this.updateUI();
    }

    public void MoveNoteName(String fileString, String rename) {
        DefaultMutableTreeNode defaultMutableTreeNode = this.rootNode;
        String[] paths = this.parseFile(fileString);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) defaultMutableTreeNode.getPath()[0];

        for (String path : paths) {
            node = this.FindTreeNote(node, path);
            if (node == null) {
                return;
            }
        }

        node.setUserObject(rename);
    }

    public String GetSelectFile() {
        TreePath paths = this.getSelectionPath();
        ArrayList<String> pathList = new ArrayList<>();
        TreePath lastTreePath = paths;
        DefaultMutableTreeNode lastNode;

        do {
            try {
                lastNode = (DefaultMutableTreeNode) lastTreePath.getLastPathComponent();
            } catch (Exception var6) {
                return "";
            }

            pathList.add((String) lastNode.getUserObject());
            lastTreePath = lastTreePath.getParentPath();
        } while (lastTreePath != null);

        pathList.remove(pathList.size() - 1);
        Collections.reverse(pathList);
        if (pathList.size() > 0) {
            return this.parseFile(pathList);
        } else {
            return "";
        }
    }

    public void AddNote(String pathString) {
        if (!pathString.trim().isEmpty()) {
            DefaultMutableTreeNode defaultMutableTreeNode = this.rootNode;
            String[] paths = this.parseFile(pathString);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) defaultMutableTreeNode.getPath()[0];
            DefaultMutableTreeNode lastTreeNode = node;
            boolean findSate = true;

            for (String path : paths) {
                DefaultMutableTreeNode _note;
                if (findSate) {
                    node = this.FindTreeNote(node, path);
                    if (node == null) {
                        findSate = false;
                        _note = new DefaultMutableTreeNode(path);
                        lastTreeNode.add(_note);
                        lastTreeNode = _note;
                    } else {
                        lastTreeNode = node;
                    }
                } else {
                    _note = new DefaultMutableTreeNode(path);
                    lastTreeNode.add(_note);
                    lastTreeNode = _note;
                }
            }

            if (lastTreeNode != null) {
                this.expandPath(new TreePath(((DefaultMutableTreeNode) lastTreeNode.getParent()).getPath()));
            }

            this.updateUI();
        }
    }

    private String[] parseFile(String fileString) {
        fileString = this.replaceSpecial(fileString);
        String[] retStrings;
        if (fileString.charAt(0) == '/') {
            retStrings = fileString.split("/");
            if (retStrings.length == 0) {
                retStrings = new String[]{"/"};
            } else {
                retStrings[0] = "/";
            }
        } else {
            retStrings = fileString.split("/");
        }

        return retStrings;
    }

    private String parseFile(ArrayList<String> pathList) {
        StringBuilder builder = new StringBuilder();

        for (String o : pathList) {
            builder.append("/");
            builder.append(o);
        }

        return this.replaceSpecial(builder.substring(1));
    }

    private String replaceSpecial(String string) {
        return string.replaceAll("\\\\+", "/").trim().replaceAll("/+", "/").trim();
    }

    private DefaultMutableTreeNode FindTreeNote(DefaultMutableTreeNode node, String noteString) {
        Enumeration e = node.children();

        while (e.hasMoreElements()) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
            if (n.getUserObject().equals(noteString)) {
                return n;
            }
        }

        return null;
    }

    private class RightClickEvent extends MouseAdapter {
        private final DataTree jTree;
        private ActionDblClick actionDblClick;
        private JPopupMenu jPopupMenu;

        public RightClickEvent(DataTree tree) {
            this.jTree = tree;
        }

        public ActionDblClick getActionDblClick() {
            return this.actionDblClick;
        }

        public void setActionDblClick(ActionDblClick actionDblClick) {
            this.actionDblClick = actionDblClick;
        }

        public void setChildPopupMenu(JPopupMenu popupMenu) {
            this.jPopupMenu = popupMenu;
        }

        public void mouseClicked(MouseEvent paramMouseEvent) {
            if (SwingUtilities.isRightMouseButton(paramMouseEvent)) {
                if (this.jPopupMenu != null && this.jTree.getSelectionPath() != null && ((DefaultMutableTreeNode) this.jTree.getLastSelectedPathComponent()).getChildCount() == 0 && this.jTree.GetSelectFile().indexOf("/") != -1) {
                    this.jPopupMenu.show(this.jTree, paramMouseEvent.getX(), paramMouseEvent.getY());
                }
            } else if (paramMouseEvent.getClickCount() == 2 && this.actionDblClick != null && !DataTree.this.GetSelectFile().trim().isEmpty()) {
                this.actionDblClick.dblClick(paramMouseEvent);
            }

        }
    }
}
