package modules;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class MasterServerController implements Runnable {
    Socket iSocket = null;
    ObjectInputStream iInStream = null;
    Thread iThread = null;
    ArrayList<FileContainer> iResources = null;
    MasterServerUI iUI = null;

    MasterServerController(Socket pSocket, ArrayList<FileContainer> pResources, MasterServerUI pUI) {
        iSocket = pSocket;
        iResources = pResources;
        iUI = pUI;

        try {
            iInStream = new ObjectInputStream(iSocket.getInputStream());
            iThread = new Thread(this);
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB MasterServerController's constructor: ");
            err.printStackTrace();

            iUI.showDialog("An error occurred while getting the resource from FILE-SERVER!");
            iSocket = null;
        }
    }

    public void startThread() {
        iThread.start();

        try {
            iThread.join();
        } catch (InterruptedException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.startThread(): ");
            err.printStackTrace();

            iUI.showDialog("An error occurred while getting the resource from FILE-SERVER!");
            iSocket = null;
        }
    }

    private synchronized void handleFilesServer(Package pBox) {
        FileContainer container = (FileContainer) pBox.getiContent();
        // Xóa toàn bộ file mà container này có
        for (int i = 0; i < iResources.size(); ++i) {
            if (iResources.get(i).getiFileServer().getiAddress().equals(container.getiFileServer().getiAddress())) {
                iResources.remove(i);
                break;
            }
        }
        iResources.add(container); // thêm container mới vào
        iUI.updateiFilesTbl(iResources);
    }

    private void handleClient(Package pBox) {
        try {
            ObjectOutputStream shipper = new ObjectOutputStream(iSocket.getOutputStream());
            Package pkg = new Package(MasterServer.LABEL, "New file-server is connecting", iResources);
            shipper.writeObject(pkg);
            shipper.close();
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.run(): ");
            err.printStackTrace();

            iUI.showDialog("An error occurred while sending the resource to CLIENT!");
        }
    }

    public void run() {
        try {
            Package box = (Package) iInStream.readObject();

            if (box.getiService().equals(FileServer.LABEL)) {
                if (box.getiMessage().equals("CONNECT")) {
                    handleFilesServer(box);
                }
            } else if (box.getiService().equals(Client.LABEL)) {
                handleClient(box);
            }

            iSocket.close();
        } catch (IOException | ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.run(): ");
            err.printStackTrace();

            iUI.showDialog("An error occurred while getting the resource from FILE-SERVER!");
        }

        iSocket = null;
        iThread.interrupt();
    }
}


public class MasterServer implements Runnable {
    public static final String LABEL = "MASTER";
    private static HostInfo iLocal;
    private static ServerSocket iMaster = null;
    private static Thread iThread;
    private static ArrayList<Activity> iActivities;
    private static ArrayList<FileContainer> iResources;
    private static MasterServerUI iUI = null;

    public MasterServer(MasterServerUI pUI) {
        if (iThread == null) {
            iThread = new Thread(this);
        }

        iUI = pUI;
        iLocal = Utils.loadHostInfo("./config/master.txt");
        iResources = new ArrayList<>();
    }

    public void run() {
        startServer();
        while (iMaster != null) { // MASTER-SERVER khởi chạy thành công
            try {
                Socket socket = iMaster.accept();
                MasterServerController handler = new MasterServerController(socket, iResources, iUI);
                handler.startThread();
            } catch (IOException err) {
                System.out.print("\uD83D\uDEAB MasterServer.run(): ");
                err.printStackTrace();

                try {
                    iMaster.close();
                } catch (IOException errr) {
                    System.out.print("\uD83D\uDEAB MasterServer.run(): ");
                    errr.printStackTrace();
                }

                iUI.showDialog("An error occurred while getting the resource from FILE-SERVER!");
            }
        }
    }

    public void startThread() {
        iThread.start();
    }

    private void startServer() {
        try {
            iMaster = new ServerSocket(iLocal.getiPort());
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB MasterServer.run(): ");
            err.printStackTrace();

            iMaster = null;
            iUI.showDialog("MASTER-SERVER launch failed!");
        }

        if (iMaster != null) {
            iUI.updateiStatusLbl(String.format("MASTER-SERVER is running on %s:%d", iLocal.getiAddress(), iLocal.getiPort()));
        }
    }
}
