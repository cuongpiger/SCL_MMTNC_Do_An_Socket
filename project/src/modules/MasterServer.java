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

    MasterServerController(Socket pSocket) {
        iSocket = pSocket;

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
        iEditor.setRowCount(0);

        try {
            Package box = (Package) iInStream.readObject();
            FileContainer container = (FileContainer) box.getiContent();

            if (box.getiService().equals(FileServer.LABEL)) {
                ArrayList<FileDetails> files = container.getiFiles();
                HostInfo host = container.getiFilerServer();
                getFiles(files, host);
            }

            iSocket.close();
        } catch (IOException | ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.run(): ");
            err.printStackTrace();
        }

        for (int i = 0; i < iFiles.size(); ++i) {
            var host = iFiles.get(i).getiHost();
            var file = iFiles.get(i).getiFile();

            iEditor.addRow(new Object[] {
                    Integer.toString(i + 1),
                    file.getiName(),
                    file.getiSize() + " bytes",
                    String.format("%s:%d", host.getiAddress(), host.getiPort())
            });
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
    protected static ArrayList<Activity> iActivities;
    protected static ArrayList<FileInfo> iFiles;
    protected static DefaultTableModel iEditor;

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

        while (flag) {
            try {
                Socket socket = iMaster.accept();
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
