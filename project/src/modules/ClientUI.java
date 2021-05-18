package modules;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class ClientUI {
    private JTable iFilesTbl;
    private JTable iDownloadTbl;
    private JTextField iDownFileTbx;
    private JButton iDownloadBtn;
    private JButton iRefreshBtn;
    private static Client iHandler;
    public JPanel iMainPnl;
    public DefaultTableModel iDownloadEditor;
    public DefaultTableModel iFilesEditor;

    private void setupiDownloadTbl() {
        iDownloadTbl.setModel(new DefaultTableModel(null, new String[] {"#ID", "Filename", "Status", "Size", "Address File-Server"}));
        TableColumnModel columns = iDownloadTbl.getColumnModel();
        iDownloadEditor = (DefaultTableModel) iDownloadTbl.getModel();
        DefaultTableCellRenderer render_col = new DefaultTableCellRenderer();
        render_col.setHorizontalAlignment(JLabel.RIGHT);
        columns.getColumn(0).setMinWidth(20);
        columns.getColumn(0).setMaxWidth(40);
        columns.getColumn(2).setMinWidth(100);
        columns.getColumn(2).setMaxWidth(100);
        columns.getColumn(3).setMinWidth(100);
        columns.getColumn(3).setMaxWidth(200);
        columns.getColumn(4).setMinWidth(150);
        columns.getColumn(4).setMaxWidth(150);
        columns.getColumn(3).setCellRenderer(render_col);
        columns.getColumn(4).setCellRenderer(render_col);
        iDownloadTbl.setModel(iDownloadEditor);
        iDownloadEditor.fireTableDataChanged();
    }

    private void setupiFilesTbl() {
        iFilesTbl.setModel(new DefaultTableModel(null, new String[] {"#ID", "Filename", "Size", "Address File-Server"}));
        TableColumnModel columns = iFilesTbl.getColumnModel();
        iFilesEditor = (DefaultTableModel) iFilesTbl.getModel();
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
        iFilesTbl.setModel(iFilesEditor);
        iFilesEditor.fireTableDataChanged();
    }

    public ClientUI(Client pHandler) {
        iHandler = pHandler;

        setupiFilesTbl();
        setupiDownloadTbl();
        iHandler.startThread(iFilesEditor, iDownloadEditor);
//        iHandler.addRowFilesTbl();
    }
}
