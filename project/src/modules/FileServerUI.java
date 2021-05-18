package modules;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FileServerUI extends JFrame implements ActionListener {
    public JPanel iMainPnl;
    private JLabel iStatusLbl;
    private JTable iFilesTbl;
    private JTable iActivitiesTbl;
    private JButton iStartBtn;
    private JButton iConnectBtn;
    private static FileServer iHandler = null;
    private static DefaultTableModel iFilesEditor = null;

    private void setupiFilesTbl() {
        iFilesTbl.setModel(new DefaultTableModel(null, new String[] {"#ID", "Filename", "Size"}));
        iFilesEditor = (DefaultTableModel) iFilesTbl.getModel();
        iFilesEditor.fireTableDataChanged();
        TableColumnModel columns = iFilesTbl.getColumnModel();
        DefaultTableCellRenderer render_col = new DefaultTableCellRenderer();
        render_col.setHorizontalAlignment(JLabel.RIGHT);
        columns.getColumn(0).setMinWidth(20);
        columns.getColumn(0).setMaxWidth(40);
        columns.getColumn(2).setMinWidth(100);
        columns.getColumn(2).setMaxWidth(200);
        columns.getColumn(2).setCellRenderer(render_col);
    }

    private void updateiFilesTbl(ArrayList<FileDetails> pFiles) {
        if (pFiles != null) {
            iFilesEditor.getDataVector().removeAllElements(); // clear content in iFilesEditor

            for (int i = 0; i < pFiles.size(); ++i) {
                FileDetails file = pFiles.get(i);
                iFilesEditor.addRow(new Object[] {
                        Integer.toString(i + 1),
                        file.getiName(),
                        String.format("%d bytes", file.getiSize())
                });
            }
        }
    }

    private void setupActionListeners() {
        iConnectBtn.addActionListener(this);
        iStartBtn.addActionListener(this);
    }

    public void showDialog(String pText) {
        JOptionPane.showMessageDialog(this, pText);
    }

    public void updateiStatusLbl(String pText) {
        iStatusLbl.setText(pText);
    }

    public void actionPerformed(ActionEvent pEvent) {
        if (pEvent.getSource() == iStartBtn) {
            if (iHandler == null) { // ko thể chạy khi FILE-SERVER chưa gửi tài nguyên đến cho MASTER-SERVER
                showDialog("FILE-SERVER cannot be run because the resources have not been sent to MASTER-SERVER");
                return;
            }


            if (iStartBtn.getText().equals("START")) { // khởi động FILE-SERVER sẵn sàng cho CLIENT download files
                // run server
                iStartBtn.setText("STOP");
                iStartBtn.setBackground(Color.RED);
                
                return;
            }

            if (iStartBtn.getText().equals("STOP")) { // tạm dừng FILE-SERVER
                // stop server
                iStartBtn.setText("START");
                iStartBtn.setBackground(Color.BLUE);

                return;
            }
        }

        if (pEvent.getSource() == iConnectBtn) {
            iHandler = new FileServer(this);
            ArrayList<FileDetails> files = iHandler.getiFiles();
            updateiFilesTbl(files); // update iFilesTbl
            iHandler.startThread("SEND-FILES-TO-MASTER");
        }
    }

    public FileServerUI() {
        super("File Server");
        setupiFilesTbl();
        setupActionListeners();
    }
}
