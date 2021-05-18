package modules;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MasterServerUI extends JFrame implements ActionListener {
    private JPanel iMainPnl;
    private DefaultTableModel iFilesEditor = null;
    private JTable iFilesTbl;
    private JTable iActivitiesTbl;
    private JButton iStartBtn;
    private JLabel iStatusLbl;
    private static MasterServer iHandler = null;

    private void setupiFilesTbl() {
        iFilesTbl.setModel(new DefaultTableModel(null, new String[]{"#ID", "Filename", "Size", "Address File-Server"}));
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

    private void setupActionListeners() {
        iStartBtn.addActionListener(this);
    }

    public void showDialog(String pText) {
        JOptionPane.showMessageDialog(this, pText);
    }

    public void cleariFilesTbl() {
        iFilesEditor.getDataVector().removeAllElements(); // clear content in iFilesEditor
    }

    public void updateiFilesTbl(ArrayList<FileContainer> pResources) {
        iFilesEditor.getDataVector().removeAllElements(); // clear content in iFilesEditor
        iFilesEditor.setRowCount(0);

        for (FileContainer resource : pResources) {
            HostInfo host = resource.getiFileServer();
            ArrayList<FileDetails> files = resource.getiFiles();
            String address = String.format("%s:%d", host.getiAddress(), host
            .getiPort());

            for (int i = 0; i < files.size(); ++i) {
                var file = files.get(i);
                iFilesEditor.addRow(new Object[] {
                        Integer.toString(i + 1),
                        file.getiName(),
                        String.format("%d bytes", file.getiSize()),
                        address
                });
            }
        }

        iFilesEditor.fireTableDataChanged();
    }

    public void updateiStatusLbl(String pText) {
        iStatusLbl.setText(pText);
    }

    public void actionPerformed(ActionEvent pEvent) {
        if (pEvent.getSource() == iStartBtn) {
            if (iStartBtn.getText().equals("START")) {
                if (iHandler == null) {
                    iHandler = new MasterServer(this);
                    iHandler.startThread();
                }

                iStartBtn.setText("CLOSE");
                iStartBtn.setBackground(Color.RED);
            } else if (iStartBtn.getText().equals("CLOSE")) {
                System.exit(0);
            }
        }
    }

    public MasterServerUI(String pTitle) {
        super(pTitle);
        setContentPane(iMainPnl);
        setupiFilesTbl();
        setupActionListeners();

    }
}
