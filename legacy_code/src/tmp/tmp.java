import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class MasterServer extends JFrame implements ActionListener, Runnable {
    private static HostInfo iMasterServer = null; // master sever

    private static JLabel sStateLbl;
    private static JButton sExitBtn;
    private static DefaultTableModel table;

    public MasterServer(HostInfo pMasterSV) {
        super("Master Server");

        iMasterServer = pMasterSV;
        JPanel btnPnl = new JPanel();
        JPanel lblPnl = new JPanel();

        sStateLbl = new JLabel("IP-Address: " + iMasterServer.getHostname() + ":" + iMasterServer.getPort());
        lblPnl.add(sStateLbl);
        add(lblPnl, BorderLayout.NORTH);

        table = new DefaultTableModel();
        JTable tbl = new JTable(table);
        table.addColumn("#ID");
        table.addColumn("Filename");
        table.addColumn("Size");
        table.addColumn("File-server address");
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        sExitBtn = new JButton("Exit");
        sExitBtn.addActionListener(this);
        btnPnl.add(sExitBtn);
        add(btnPnl, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == sExitBtn) {
            System.exit(0);
        }
    }

    public void run() {

    }

    public static void main(String[] args) {
        HostInfo master = Utils.getHostInfo("./config/master_info.txt");
        MasterServer frame = new MasterServer(master);

        frame.setSize(500, 300);
        frame.setVisible(true);
        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent ev) {
                        System.exit(0);
                    }
                }
        );
    }
}
