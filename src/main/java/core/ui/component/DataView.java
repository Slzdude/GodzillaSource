package core.ui.component;

import core.ui.imp.ActionDblClick;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

public class DataView extends JTable {
   private static final long serialVersionUID = -8531006713898868252L;
   private JPopupMenu rightClickMenu;
   private RightClickEvent rightClickEvent;
   private int imgColumn;

   private void initJtableConfig() {
      this.rightClickEvent = new RightClickEvent(this.rightClickMenu, this);
      this.addMouseListener(this.rightClickEvent);
      this.setSelectionMode(0);
      this.setAutoCreateRowSorter(true);
      this.setRowHeight(25);
   }

   public DataView(Vector rowData, Vector columnNames, int imgColumn, int imgMaxWidth) {
      super(rowData, columnNames);
      this.initJtableConfig();
      this.imgColumn = imgColumn;
      if (imgColumn >= 0) {
         this.getColumnModel().getColumn(0).setMaxWidth(imgMaxWidth);
      }

   }

   public void setActionDblClick(ActionDblClick actionDblClick) {
      if (this.rightClickEvent != null) {
         this.rightClickEvent.setActionListener(actionDblClick);
      }

   }

   public JPopupMenu getRightClickMenu() {
      return this.rightClickMenu;
   }

   public void RemoveALL() {
      DefaultTableModel defaultTableModel = this.getModel();

      while(defaultTableModel.getRowCount() > 0) {
         defaultTableModel.removeRow(0);
      }

      this.updateUI();
   }

   public Class getColumnClass(int column) {
      return column == this.imgColumn ? Icon.class : Object.class;
   }

   public Vector GetSelectRow() {
      int select_row_id = this.getSelectedRow();
      if (select_row_id == -1) {
         return null;
      } else {
         int column_num = this.getColumnCount();
         Vector vector = new Vector();

         for(int i = 0; i < column_num; ++i) {
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
         columnVector = (Vector)field.get(tableModel);
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

         for(int i = 0; i < column_num; ++i) {
            Object value = this.getValueAt(select_row_id, i);
            if (value instanceof String) {
               select_row_columns[i] = (String)value;
            }
         }

         return select_row_columns;
      } else {
         return null;
      }
   }

   public DefaultTableModel getModel() {
      return super.dataModel != null ? (DefaultTableModel)super.dataModel : null;
   }

   public synchronized void AddRow(Object object) {
      Class class1 = object.getClass();
      Field[] fields = class1.getFields();
      String field_name = null;
      String field_value = null;
      DefaultTableModel tableModel = this.getModel();
      Vector rowVector = new Vector(tableModel.getColumnCount());
      String[] columns = new String[tableModel.getColumnCount()];

      for(int i = 0; i < tableModel.getColumnCount(); ++i) {
         columns[i] = tableModel.getColumnName(i).toUpperCase();
         rowVector.add("NULL");
      }

      Field[] var12 = fields;
      int var11 = fields.length;

      for(int var10 = 0; var10 < var11; ++var10) {
         Field field = var12[var10];
         field_name = field.getName();
         int find_id = Arrays.binarySearch(columns, field_name.substring(2, field_name.length()).toUpperCase());
         if (field_name.substring(0, 2).equals("s_") && find_id != -1) {
            try {
               if (field.get(object) instanceof String) {
                  field_value = (String)field.get(object);
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
      String field_name = null;
      String field_value = null;
      DefaultTableModel tableModel = this.getModel();
      Vector rowVector = (Vector)tableModel.getDataVector().get(row_id);
      String[] columns = new String[tableModel.getColumnCount()];

      for(int i = 0; i < tableModel.getColumnCount(); ++i) {
         columns[i] = tableModel.getColumnName(i).toUpperCase();
      }

      Field[] var13 = fields;
      int var12 = fields.length;

      for(int var11 = 0; var11 < var12; ++var11) {
         Field field = var13[var11];
         field_name = field.getName();
         int find_id = Arrays.binarySearch(columns, field_name.substring(2, field_name.length()).toUpperCase());
         if (field_name.substring(0, 2).equals("s_") && find_id != -1) {
            try {
               if (field.get(object) instanceof String) {
                  field_value = (String)field.get(object);
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
         sorter = new TableRowSorter((DefaultTableModel)super.getModel());
         this.setRowSorter(sorter);
         sorter.setRowFilter(RowFilter.regexFilter(regxString, new int[0]));
      } else {
         sorter = new TableRowSorter((DefaultTableModel)super.getModel());
         this.setRowSorter(sorter);
         sorter.setRowFilter((RowFilter)null);
      }

   }

   public void setRightClickMenu(JPopupMenu rightClickMenu) {
      this.rightClickMenu = rightClickMenu;
      this.rightClickEvent.setRightClickMenu(rightClickMenu);
   }

   public JTableHeader getTableHeader() {
      JTableHeader tableHeader = super.getTableHeader();
      tableHeader.setReorderingAllowed(false);
      DefaultTableCellRenderer hr = (DefaultTableCellRenderer)tableHeader.getDefaultRenderer();
      hr.setHorizontalAlignment(0);
      return tableHeader;
   }

   public TableCellRenderer getDefaultRenderer(Class columnClass) {
      DefaultTableCellRenderer cr = (DefaultTableCellRenderer)super.getDefaultRenderer(columnClass);
      cr.setHorizontalAlignment(0);
      return cr;
   }

   public boolean isCellEditable(int paramInt1, int paramInt2) {
      return false;
   }

   private class RightClickEvent extends MouseAdapter {
      private JPopupMenu rightClickMenu;
      private DataView dataView;
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
