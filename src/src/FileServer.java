import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


class FilesPanel extends JPanel {
    private static JTable jtable = null;

    public FilesPanel(ArrayList<FileDetails> pFiles) {
        DefaultTableModel model = new DefaultTableModel();
        jtable = new JTable(model);

        model.addColumn("#ID");
        model.addColumn("Filename");
        model.addColumn("Size");

        for (int i = 0; i < pFiles.size(); ++i) {
            var tmp = pFiles.get(i);
            model.addRow(new Object[]{i + 1, tmp.getFile_name(), tmp.getSizeFormat()});
        }

        add(new JScrollPane(jtable), BorderLayout.CENTER);
    }
}

class ColorsPanel extends JPanel {
    private JTextArea display;

    public ColorsPanel() {
        display = new JTextArea(12, 43);
        display.setWrapStyleWord(true);
        display.setLineWrap(true);

        add(new JScrollPane(display), BorderLayout.PAGE_START);
    }
}

public class FileServer extends JFrame implements ActionListener {
    private static HostInfo master = null; // master sever
    private static HostInfo host = null; // file server
    private static FileServerHandler file_server = null;

    private JLabel state;
    private JTabbedPane jtp;

    private JButton time_btn;
    private JButton exit_btn;
    private JPanel button_pnl;
    private final static String CONFIG_FILE = "./FS_config/host_info.txt";

    public FileServer(HostInfo pHost, HostInfo pMaster, ArrayList<FileDetails> pFiles) {
        super("File Server");
        host = pHost;
        master = pMaster;
        file_server = new FileServerHandler(host, master, pFiles);

        state = new JLabel("IP-Address: " + pHost.getHostname() + ":" + pHost.getPort());
        add(state, BorderLayout.NORTH);

        jtp = new JTabbedPane();
        jtp.addTab("List files", new FilesPanel(pFiles));
        jtp.addTab("Colors", new ColorsPanel());
        add(jtp, BorderLayout.CENTER);

//        display = new JTextArea(10, 15);
//        display.setWrapStyleWord(true);
//        display.setLineWrap(true);
//        add(new JScrollPane(display), BorderLayout.CENTER);

        button_pnl = new JPanel();
        exit_btn = new JButton("Exit");
        exit_btn.addActionListener(this);
        button_pnl.add(exit_btn);
        add(button_pnl, BorderLayout.SOUTH);

        file_server.talkToMasterServer();
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == exit_btn) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        HostInfo host = Utils.getHostInfo("./config/host_info.txt"); // load thông tin về host IP và port của file server
        HostInfo master = Utils.getHostInfo("./config/master_info.txt");
        ArrayList<FileDetails> files = Utils.loadResources(FileServerHandler.RESOURCES);
        FileServer frame = new FileServer(host, master, files);

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
