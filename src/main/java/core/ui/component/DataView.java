package core.ui.component;

import core.ui.imp.ActionDblClick;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;

public class DataView extends JTable {
    private static final long serialVersionUID = -8531006713898868252L;
    private final int imgColumn;
    private JPopupMenu rightClickMenu;
    private RightClickEvent rightClickEvent;

    public DataView(Vector rowData, Vector columnNames, int imgColumn, int imgMaxWidth) {
        super(rowData, columnNames);
        this.initJtableConfig();
        this.imgColumn = imgColumn;
        if (imgColumn >= 0) {
            this.getColumnModel().getColumn(0).setMaxWidth(imgMaxWidth);
        }

    }

    private void initJtableConfig() {
        this.rightClickEvent = new RightClickEvent(this.rightClickMenu, this);
        this.addMouseListener(this.rightClickEvent);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setAutoCreateRowSorter(true);
        this.setRowHeight(25);
    }

    public void setActionDblClick(ActionDblClick actionDblClick) {
        if (this.rightClickEvent != null) {
            this.rightClickEvent.setActionListener(actionDblClick);
        }

    }

    public JPopupMenu getRightClickMenu() {
        return this.rightClickMenu;
    }

    public void setRightClickMenu(JPopupMenu rightClickMenu) {
        this.rightClickMenu = rightClickMenu;
        this.rightClickEvent.setRightClickMenu(rightClickMenu);
    }

    public void RemoveALL() {
        DefaultTableModel defaultTableModel = this.getModel();

        while (defaultTableModel.getRowCount() > 0) {
            defaultTableModel.removeRow(0);
        }

        this.updateUI();
    }

    public Class getColumnClass(int column) {
        return column == this.imgColumn ? Icon.class : Object.class;
    }

    public Vector<Object> GetSelectRow() {
        int select_row_id = this.getSelectedRow();
        if (select_row_id == -1) {
            return null;
        } else {
            int column_num = this.getColumnCount();
            Vector<Object> vector = new Vector<>();

            for (int i = 0; i < column_num; ++i) {
                vector.add(this.getValueAt(select_row_id, i));
            }

            return vector;
        }
    }

    public Vector getColumnVector() {
        Vector columnVector = null;

        try {
            DefaultTableModel tableModel = this.getModel();
            Field field = tableModel.getClass().getDeclaredField("columnIdentifiers");
            field.setAccessible(true);
            columnVector = (Vector) field.get(tableModel);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return columnVector;
    }

    public String[] GetSelectRow1() {
        int select_row_id = this.getSelectedRow();
        if (select_row_id != -1) {
            int column_num = this.getColumnCount();
            String[] select_row_columns = new String[column_num];

            for (int i = 0; i < column_num; ++i) {
                Object value = this.getValueAt(select_row_id, i);
                if (value instanceof String) {
                    select_row_columns[i] = (String) value;
                }
            }

            return select_row_columns;
        } else {
            return null;
        }
    }

    public DefaultTableModel getModel() {
        return super.dataModel != null ? (DefaultTableModel) super.dataModel : null;
    }

    public synchronized void AddRow(Object object) {
        Class class1 = object.getClass();
        Field[] fields = class1.getFields();
        String field_name;
        String field_value;
        DefaultTableModel tableModel = this.getModel();
        Vector<String> rowVector = new Vector<>(tableModel.getColumnCount());
        String[] columns = new String[tableModel.getColumnCount()];

        for (int i = 0; i < tableModel.getColumnCount(); ++i) {
            columns[i] = tableModel.getColumnName(i).toUpperCase();
            rowVector.add("NULL");
        }

        Field[] var12 = fields;
        int var11 = fields.length;

        for (int var10 = 0; var10 < var11; ++var10) {
            Field field = var12[var10];
            field_name = field.getName();
            int find_id = Arrays.binarySearch(columns, field_name.substring(2).toUpperCase());
            if (field_name.startsWith("s_") && find_id != -1) {
                try {
                    if (field.get(object) instanceof String) {
                        field_value = (String) field.get(object);
                    } else {
                        field_value = "NULL";
                    }
                } catch (Exception var15) {
                    field_value = "NULL";
                }

                rowVector.set(find_id, field_value);
            }
        }

        tableModel.addRow(rowVector);
    }

    public synchronized void AddRow(Vector one_row) {
        DefaultTableModel tableModel = this.getModel();
        tableModel.addRow(one_row);
    }

    public synchronized void AddRows(Vector rows) {
        DefaultTableModel tableModel = this.getModel();
        Vector columnVector = this.getColumnVector();
        tableModel.setDataVector(rows, columnVector);
    }

    public synchronized void SetRow(int row_id, Object object) {
        Class class1 = object.getClass();
        Field[] fields = class1.getFields();
        String field_name;
        String field_value;
        DefaultTableModel tableModel = this.getModel();
        Vector rowVector = (Vector) tableModel.getDataVector().get(row_id);
        String[] columns = new String[tableModel.getColumnCount()];

        for (int i = 0; i < tableModel.getColumnCount(); ++i) {
            columns[i] = tableModel.getColumnName(i).toUpperCase();
        }

        Field[] var13 = fields;
        int var12 = fields.length;

        for (int var11 = 0; var11 < var12; ++var11) {
            Field field = var13[var11];
            field_name = field.getName();
            int find_id = Arrays.binarySearch(columns, field_name.substring(2).toUpperCase());
            if (field_name.startsWith("s_") && find_id != -1) {
                try {
                    if (field.get(object) instanceof String) {
                        field_value = (String) field.get(object);
                    } else {
                        field_value = "NULL";
                    }
                } catch (Exception var16) {
                    field_value = "NULL";
                }

                rowVector.set(find_id, field_value);
            }
        }

    }

    public void find(String regxString) {
        TableRowSorter sorter;
        if (!regxString.isEmpty()) {
            sorter = new TableRowSorter(super.getModel());
            this.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter(regxString));
        } else {
            sorter = new TableRowSorter(super.getModel());
            this.setRowSorter(sorter);
            sorter.setRowFilter(null);
        }

    }

    public JTableHeader getTableHeader() {
        JTableHeader tableHeader = super.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        DefaultTableCellRenderer hr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        hr.setHorizontalAlignment(SwingConstants.CENTER);
        return tableHeader;
    }

    public TableCellRenderer getDefaultRenderer(Class columnClass) {
        DefaultTableCellRenderer cr = (DefaultTableCellRenderer) super.getDefaultRenderer(columnClass);
        cr.setHorizontalAlignment(SwingConstants.CENTER);
        return cr;
    }

    public boolean isCellEditable(int paramInt1, int paramInt2) {
        return false;
    }

    private class RightClickEvent extends MouseAdapter {
        private final DataView dataView;
        private JPopupMenu rightClickMenu;
        private ActionDblClick actionDblClick;

        public RightClickEvent(JPopupMenu rightClickMenu, DataView jtable) {
            this.rightClickMenu = rightClickMenu;
            this.dataView = jtable;
        }

        public void setRightClickMenu(JPopupMenu rightClickMenu) {
            this.rightClickMenu = rightClickMenu;
        }

        public void setActionListener(ActionDblClick event) {
            this.actionDblClick = event;
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == 3) {
                if (this.rightClickMenu != null) {
                    int i = this.dataView.rowAtPoint(mouseEvent.getPoint());
                    if (i != -1) {
                        this.rightClickMenu.show(this.dataView, mouseEvent.getX(), mouseEvent.getY());
                        this.dataView.setRowSelectionInterval(i, i);
                    }
                }
            } else if (mouseEvent.getClickCount() == 2 && this.actionDblClick != null) {
                this.actionDblClick.dblClick(mouseEvent);
            }

        }
    }
}
