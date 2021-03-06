package modules;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientUI extends JFrame implements ActionListener {
    private JPanel iMainPnl;
    private JTable iFilesTbl;
    private JTable iDownloadTbl;
    private JTextField iDownFileTbx;
    private JButton iDownloadBtn;
    private JButton iRefreshBtn;
    private static Client iHandler = null;
    private static DefaultTableModel iDownloadEditor;
    private static DefaultTableModel iFilesEditor;

    private void setupiDownloadTbl() {
        iDownloadTbl.setModel(new DefaultTableModel(null, new String[] {"#ID", "Filename", "Status", "Size"}));
        iDownloadEditor = (DefaultTableModel) iDownloadTbl.getModel();
        TableColumnModel columns = iDownloadTbl.getColumnModel();
        DefaultTableCellRenderer render_col = new DefaultTableCellRenderer();
        render_col.setHorizontalAlignment(JLabel.RIGHT);
        columns.getColumn(0).setMinWidth(20);
        columns.getColumn(0).setMaxWidth(40);
        columns.getColumn(2).setMinWidth(150);
        columns.getColumn(2).setMaxWidth(200);
        columns.getColumn(3).setMinWidth(100);
        columns.getColumn(3).setMaxWidth(200);
        columns.getColumn(3).setCellRenderer(render_col);
        iDownloadTbl.setModel(iDownloadEditor);
    }

    public void addNewRowToDownloadTbl(FileDetails pFile) {
        int no_rows = iDownloadTbl.getRowCount();
        iDownloadEditor.addRow(new Object[] {no_rows + 1, pFile.getiName(), "Downloading", String.format("%d bytes", pFile.getiSize())});
    }

    public int getRowCountDownloadTbl() {
        return iDownloadTbl.getRowCount();
    }

    public void updateStatusDownloadTbl(int id, String pText) {
        iDownloadTbl.setValueAt(pText, id, 2);
    }

    private void setupiFilesTbl() {
        iFilesTbl.setModel(new DefaultTableModel(null, new String[] {"#ID", "Filename", "Size", "Address File-Server"}));
        iFilesEditor = (DefaultTableModel) iFilesTbl.getModel();
        TableColumnModel columns = iFilesTbl.getColumnModel();
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
    }

    private void setupActionListerners() {
        iRefreshBtn.addActionListener(this);
        iDownloadBtn.addActionListener(this);
    }


    public void actionPerformed(ActionEvent pEvent) {
        if (pEvent.getSource() == iRefreshBtn) {
            if (iHandler == null) {
                iHandler = new Client(this);
                iHandler.startClient("REFRESH");
            } else {
                iHandler.startClient("REFRESH");
            }

            iDownFileTbx.setText("");
            iDownFileTbx.setEditable(true);
            iDownloadBtn.setEnabled(true);
        }

        if (pEvent.getSource() == iDownloadBtn) {
            try {
                int id_file = Integer.parseInt(iDownFileTbx.getText());
                int no_rows = iFilesTbl.getRowCount();

                if (id_file > 0 && id_file <= no_rows) {
                    iDownFileTbx.setText("");
                    id_file -= 1;
                    String filename = iFilesTbl.getValueAt(id_file,1).toString();
                    String[] file_server = iFilesTbl.getValueAt(id_file,3).toString().split(":");

                    if (iHandler != null && iHandler.canDownload()) {
                        HostInfo host = new HostInfo(file_server[0], Integer.parseInt(file_server[1]));
                        ClientController downloader = new ClientController(host, filename, this);
                        downloader.startThread();
                    }
                } else {
                    showDialog("Your file's ID does not exist!");
                    iDownFileTbx.setText("");
                }
            } catch (NumberFormatException err) {
                iDownFileTbx.setText("");
                showDialog("Your file's ID is not valid!");
            }
        }
    }

    public void showDialog(String pText) {
        JOptionPane.showMessageDialog(this, pText);
    }

    public void updateiFilesTbl(ArrayList<FileContainer> pResources) {
        if (pResources != null) {
            iFilesEditor.getDataVector().removeAllElements();

            int id = 1;
            for (FileContainer resource : pResources) {
                HostInfo host = resource.getiFileServer();
                ArrayList<FileDetails> files = resource.getiFiles();
                String address = String.format("%s:%d", host.getiAddress(), host
                        .getiPort());

                for (int i = 0; i < files.size(); ++i) {
                    var file = files.get(i);
                    iFilesEditor.addRow(new Object[] {
                            Integer.toString(id++),
                            file.getiName(),
                            String.format("%d bytes", file.getiSize()),
                            address
                    });
                }
            }
        }
    }

    public ClientUI(String pTitle) {
        super(pTitle);
        iDownFileTbx.setEditable(false);
        iDownloadBtn.setEnabled(false);
        setContentPane(iMainPnl);
        setupiFilesTbl();
        setupiDownloadTbl();
        setupActionListerners();
    }
}
