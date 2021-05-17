package modules;

import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class MasterServerController extends MasterServer implements Runnable {
    Socket iSocket;
    ObjectInputStream iInStream;
    Thread iThread;
    DefaultTableModel iiEditor;

    MasterServerController(Socket pSocket) {
        iSocket = pSocket;

        try {

            System.out.println("contrusctr");
            iInStream = new ObjectInputStream(iSocket.getInputStream());
            iThread = new Thread(this);
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB MasterServerController's constructor: ");
            err.printStackTrace();
        }
    }

    public void startThread(DefaultTableModel pEditor) {
        iiEditor = pEditor;
        this.iThread.start();

        try {
            iThread.join();
        } catch (InterruptedException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.startThread(): ");
            err.printStackTrace();
        }
    }

    public void run() {
        iiEditor.setRowCount(0);

        System.out.println("running in run() controller");
        try {
            Package box = (Package) iInStream.readObject();
            FileContainer container = (FileContainer) box.getiContent();

            if (box.getiService().equals(FileServer.LABEL)) {
                ArrayList<FileDetails> files = container.getiFiles();
                HostInfo host = container.getiFilerServer();
                getFiles(files, host);
            }
        } catch (IOException | ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.run(): ");
            err.printStackTrace();
        }

        for (int i = 0; i < iFiles.size(); ++i) {
            iiEditor.addRow(new Object[] {
                    Integer.toString(i + 1),
                    iFiles.get(i).getiFile().getiName(),
                    iFiles.get(i).getiFile().getiSize(),
                    iFiles.get(i).getiHost().getiAddress()
            });
        }
    }

    private synchronized void getFiles(ArrayList<FileDetails> pFiles, HostInfo pHost) {
        for (var f : pFiles) {
            boolean flag = false;

            for (var file : iFiles) {
                if (file.match(f, pHost.getiAddress())) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                FileInfo new_file = new FileInfo(pHost, f);
                iFiles.add(new_file);
            }
        }
    }
}


public class MasterServer implements Runnable {
    private static HostInfo iLocal;
    private static ServerSocket iMaster;
    private static Thread iThread;
    protected static ArrayList<Activity> iActivities;
    protected static ArrayList<FileInfo> iFiles;
    private static DefaultTableModel iEditor;

    public MasterServer() {
        iLocal = Utils.loadHostInfo("./config/master.txt");
        iThread = new Thread(this);
        iActivities = new ArrayList<>();
        iFiles = new ArrayList<>();
    }

    public void startThread(DefaultTableModel pEditor) {
        iEditor = pEditor;
        iThread.start();
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

        System.out.println("Master server is running");

        while (flag) {
            try {
                Socket socket = iMaster.accept();
                System.out.println("new accepted");
                MasterServerController handler = new MasterServerController(socket);
                handler.startThread(iEditor);
            } catch (IOException err) {
                System.out.print("\uD83D\uDEAB MasterServer.run(): ");
                err.printStackTrace();

                return;
            }
        }
    }
}
