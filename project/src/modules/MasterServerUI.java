package modules;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class MasterServerUI {
    public JPanel iMainPnl;
    public DefaultTableModel iEditor;
    private JTable iFilesTbl;
    private JTable iActivitiesTbl;
    private JButton iCloseBtn;
    private JLabel iStatusLbl;
    private static MasterServer iHandler;
    private static HostInfo iLocal;

    private void setupiFilesTbl() {
        iFilesTbl.setModel(new DefaultTableModel(null, new String[] {"#ID", "Filename", "Size", "Address File-Server"}));
        TableColumnModel columns = iFilesTbl.getColumnModel();
        iEditor = (DefaultTableModel) iFilesTbl.getModel();
        DefaultTableCellRenderer render_col = new DefaultTableCellRenderer();
        render_col.setHorizontalAlignment(JLabel.RIGHT);
        columns.getColumn(0).setMinWidth(20);
        columns.getColumn(0).setMaxWidth(40);
        columns.getColumn(2).setMinWidth(100);
        columns.getColumn(2).setMaxWidth(200);
        columns.getColumn(3).setMinWidth(150);
        columns.getColumn(3).setMaxWidth(150);
        columns.getColumn(2).setCellRenderer(render_col);
        columns.getColumn(3).setCellRenderer(render_col);
        iFilesTbl.setModel(iEditor);
        iEditor.fireTableDataChanged();
    }

    public MasterServerUI(MasterServer pHandler) {
        iHandler = pHandler;
        iLocal = Utils.loadHostInfo("./config/master.txt");

        setupiFilesTbl();
        iHandler.startThread(iEditor);
        iStatusLbl.setText(String.format("Master-sever is running on %s:%d", iLocal.getiAddress(), iLocal.getiPort()));
    }
}