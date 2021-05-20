import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientUI implements ActionListener {

    private static JButton eExitBtn, eDownBtn;
    private static JTextField eFileTxt;
    private static JFrame eFrame;
    private static DefaultTableModel eTable;
    private static JTabbedPane eTbp;
    public static DefaultTableModel eFilesTab, eDownTab;

    public ClientUI() {
        eFrame = new JFrame("Client");
        eFrame.setSize(500, 300);
        eFrame.setLayout(new FlowLayout());
//        setupFilesTab();
    }

    private void setupFilesTab() {
        eFilesTab = new DefaultTableModel();
        JTable tbl = new JTable(eFilesTab);
        eTable.addColumn("#ID");
        eTable.addColumn("Filename");
        eTable.addColumn("Size");
        eTable.addColumn("File-server address");

        JPanel pnl = new JPanel();
        pnl.add(tbl);
        eTbp.add("Files", pnl);
        eFrame.add(eTbp);
    }

    public void start() {
        eFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == eExitBtn) {
            System.exit(0);
        }
    }
}

//https://viettuts.vn/lap-trinh-mang-voi-java/udp-transfer-file-example
