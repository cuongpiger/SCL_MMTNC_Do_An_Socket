package modules;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class FileServerUI extends JFrame implements ActionListener {
    private JPanel iMainPnl;
    private JLabel iStatusLbl;
    private JTable iFilesTbl;
    private JTable iActivitiesTbl;
    private JButton iStartBtn;
    private JButton iConnectBtn;
    private static FileServer iHandler = null;
    private static DefaultTableModel iFilesEditor = null;
    private static DefaultTableModel iActEditor = null;


    private void setupiFilesTbl() {
        iFilesTbl.setModel(new DefaultTableModel(null, new String[]{"#ID", "Filename", "Size"}));
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
        iFilesTbl.setModel(iFilesEditor); // coi chừng dòng này
    }

    private void setupiActivitiesTbl() {
        iActivitiesTbl.setModel(new DefaultTableModel(null, new String[]{"#ID", "Client's address", "Filename", "State"}));
        iActEditor = (DefaultTableModel) iActivitiesTbl.getModel();
        iActEditor.fireTableDataChanged();
        TableColumnModel columns = iActivitiesTbl.getColumnModel();
        DefaultTableCellRenderer render_col = new DefaultTableCellRenderer();
        render_col.setHorizontalAlignment(JLabel.RIGHT);
        columns.getColumn(0).setMinWidth(20);
        columns.getColumn(0).setMaxWidth(40);
        columns.getColumn(1).setMinWidth(150);
        columns.getColumn(1).setMaxWidth(150);
        columns.getColumn(3).setMinWidth(150);
        columns.getColumn(3).setMaxWidth(200);
        columns.getColumn(1).setCellRenderer(render_col);
        iActivitiesTbl.setModel(iActEditor);
    }

    public void addNewRowToActivitiesTbl(DatagramPacket pClient, String pFilename, int pPartitions) {
        int no_rows = iActivitiesTbl.getRowCount();
        iActEditor.addRow(new Object[]{no_rows + 1, String.format("%s:%d", pClient.getAddress().toString(), pClient.getPort()), pFilename, ""});
    }

    public int getRowCountActivitiesTbl() {
        return iActivitiesTbl.getRowCount();
    }

    public void updateStateActivitiesTbl(int id, String pText) {
        iActivitiesTbl.setValueAt(pText, id, 3);
    }

    private void updateiFilesTbl(ArrayList<FileDetails> pFiles) {
        if (pFiles != null) {
            iFilesEditor.getDataVector().removeAllElements(); // clear content in iFilesEditor

            for (int i = 0; i < pFiles.size(); ++i) {
                FileDetails file = pFiles.get(i);
                iFilesEditor.addRow(new Object[]{
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
                if (iHandler != null) {
                    iHandler.start("START-FILE-SERVER");
                }

                iStartBtn.setText("CLOSE");
                iStartBtn.setBackground(Color.RED);

                return;
            }

            if (iStartBtn.getText().equals("CLOSE")) { // tạm dừng FILE-SERVER
                iHandler = new FileServer(this);
                iHandler.start("CLOSE-FILE-SERVER");

                iStartBtn.setText("START");
                iStartBtn.setBackground(Color.BLUE);

                return;
            }
        }

        if (pEvent.getSource() == iConnectBtn) {
            iHandler = new FileServer(this);
            ArrayList<FileDetails> files = iHandler.getiFiles();
            updateiFilesTbl(files); // update iFilesTbl
            iHandler.start("SEND-FILES-TO-MASTER");
            iStartBtn.setEnabled(true);
        }
    }

    public FileServerUI(String pTitle) {
        super(pTitle);
        iStartBtn.setEnabled(false);
        setContentPane(iMainPnl);
        setupiFilesTbl();
        setupiActivitiesTbl();
        setupActionListeners();
    }
}
