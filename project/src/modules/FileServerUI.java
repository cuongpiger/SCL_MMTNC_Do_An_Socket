package modules;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class FileServerUI {
    public JPanel iMainPnl;
    private JLabel iStatusLbl;
    private JTable iFilesTbl;
    private JTable iActivitiesTbl;
    private JButton iCloseBtn;
    private JButton iConnectBtn;
    private static FileServer iHandler;

    private void setupiFilesTbl() {
        iFilesTbl.setModel(new DefaultTableModel(null, new String[] {"#ID", "Filename", "Size"}));
        TableColumnModel columns = iFilesTbl.getColumnModel();
        DefaultTableModel editor = (DefaultTableModel) iFilesTbl.getModel();
        DefaultTableCellRenderer render_col = new DefaultTableCellRenderer();
        render_col.setHorizontalAlignment(JLabel.RIGHT);
        columns.getColumn(0).setMinWidth(20);
        columns.getColumn(0).setMaxWidth(40);
        columns.getColumn(2).setMinWidth(100);
        columns.getColumn(2).setMaxWidth(200);
        columns.getColumn(2).setCellRenderer(render_col);

        var files = iHandler.getiFiles();
        for (int i = 0; i < files.size(); ++i) {
            var file = files.get(i);
            editor.addRow(new Object[]{Integer.toString(i + 1),
                    file.getiName(), String.format("%d bytes", file.getiSize())});
        }
    }

    public FileServerUI(FileServer pHandler) {
        iHandler = pHandler;
        iStatusLbl.setText(String.format("File-Server is running on %s:%d", iHandler.getiLocal().getiAddress(), iHandler.getiLocal().getiPort()));

        setupiFilesTbl();
        System.out.println(">> FileServerUtil constructor" + pHandler.getiLocal().getiPort());
    }
}
