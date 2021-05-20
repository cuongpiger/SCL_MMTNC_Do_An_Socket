import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MasterServerUI implements ActionListener {
    private static MasterServerHandler iMasterServer = null;

    private static JLabel eStateLbl;
    private static JButton eExitBtn;
    public static DefaultTableModel eTable;
    private static JFrame eFrame;

    public MasterServerUI() {
        iMasterServer = new MasterServerHandler();

        eFrame = new JFrame("Master Server");
        eFrame.setSize(500, 300);

        eStateLbl = new JLabel("Master-server is running on " + iMasterServer.getiPORT());
        JPanel lblPnl = new JPanel();
        lblPnl.add(eStateLbl);
        eFrame.add(lblPnl, BorderLayout.NORTH);

        eTable = new DefaultTableModel();
        JTable tbl = new JTable(eTable);
        eTable.addColumn("#ID");
        eTable.addColumn("Filename");
        eTable.addColumn("Size");
        eTable.addColumn("File-server address");
        eFrame.add(new JScrollPane(tbl), BorderLayout.CENTER);

        eExitBtn = new JButton("Exit");
        eExitBtn.addActionListener(this);
        JPanel btnPnl = new JPanel();
        btnPnl.add(eExitBtn);
        eFrame.add(btnPnl, BorderLayout.SOUTH);
    }

    public void start() {
        eFrame.setVisible(true);
        iMasterServer.runThread();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == eExitBtn) {
            System.exit(0);
        }
    }
}