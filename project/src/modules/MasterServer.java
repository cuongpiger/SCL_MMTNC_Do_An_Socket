package modules;

import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class MasterServerController implements Runnable {
    Socket iSocket;
    ObjectInputStream iInStream;
    Thread iThread;
    ArrayList<FileInfo> iFiles;
    static DefaultTableModel iEditor;

    MasterServerController(Socket pSocket, ArrayList<FileInfo> pFiles, DefaultTableModel pEditor) {
        iSocket = pSocket;
        iFiles = pFiles;
        iEditor = pEditor;

        try {
            iInStream = new ObjectInputStream(iSocket.getInputStream());
            iThread = new Thread(this);
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB MasterServerController's constructor: ");
            err.printStackTrace();
        }
    }

    public void startThread(DefaultTableModel pEditor) {
        iThread.start();

        try {
            iThread.join();
        } catch (InterruptedException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.startThread(): ");
            err.printStackTrace();
        }
    }

    public void run() {
        try {
            Package box = (Package) iInStream.readObject();

            if (box.getiService().equals(FileServer.LABEL)) {
                iEditor.setRowCount(0);

                FileContainer container = (FileContainer) box.getiContent();
                ArrayList<FileDetails> files = container.getiFiles();
                HostInfo host = container.getiFilerServer();
                getFiles(files, host);

                for (int i = 0; i < iFiles.size(); ++i) {
                    var tmp_host = iFiles.get(i).getiHost();
                    var file = iFiles.get(i).getiFile();

                    iEditor.addRow(new Object[] {
                            Integer.toString(i + 1),
                            file.getiName(),
                            file.getiSize() + " bytes",
                            String.format("%s:%d", tmp_host.getiAddress(), tmp_host.getiPort())
                    });
                }

            } else if (box.getiService().equals(Client.LABEL)) {
                if (box.getiMessage().equals("GET-FILES")) {
                    ObjectOutputStream shipper = new ObjectOutputStream(iSocket.getOutputStream());
//                    FileImage image = new FileImage(iFiles);
                    Package box_files = new Package(MasterServer.LABEL, "New file-server is connecting", iFiles);
                    shipper.writeObject(box_files);
                    shipper.close();
                }
            }

            iSocket.close();
        } catch (IOException | ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.run(): ");
            err.printStackTrace();
        }
    }

    private synchronized void getFiles(ArrayList<FileDetails> pFiles, HostInfo pHost) {
        for (int i = 0; i < iFiles.size(); ++i) {
            if (iFiles.get(i).getiHost().getiAddress().equals(pHost.getiAddress())) {
                iFiles.remove(i);
            }
        }

        for (var file : pFiles) {
            FileInfo new_file = new FileInfo(pHost, file);
            iFiles.add(new_file);
        }
    }
}


public class MasterServer implements Runnable {
    private static HostInfo iLocal;
    private static ServerSocket iMaster;
    private static Thread iThread;
    private static ArrayList<Activity> iActivities;
    private static ArrayList<FileInfo> iFiles;
    private static DefaultTableModel iEditor;
    public static final String LABEL = "MASTER";

    public MasterServer() {
        iLocal = Utils.loadHostInfo("./config/master.txt");
        iThread = new Thread(this);
        iActivities = new ArrayList<>();
        iFiles = new ArrayList<>();
    }

    public void run() {
        boolean flag = false;

        try {
            iMaster = new ServerSocket(iLocal.getiPort());
            flag = true;
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB MasterServer.run(): ");
            err.printStackTrace();

            return;
        }

        while (flag) {
            try {
                Socket socket = iMaster.accept();
                MasterServerController handler = new MasterServerController(socket, iFiles, iEditor);
                handler.startThread(iEditor);

                System.out.println("new accepted");
            } catch (IOException err) {
                System.out.print("\uD83D\uDEAB MasterServer.run(): ");
                err.printStackTrace();

                return;
            }
        }
    }

    public void startServer(DefaultTableModel pEditor) {
        iEditor = pEditor;
        iThread.start();
    }
}
