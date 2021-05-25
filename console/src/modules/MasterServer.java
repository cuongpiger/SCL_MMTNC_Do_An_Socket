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

    MasterServerController(Socket pSocket, ArrayList<FileContainer> pResources) {
        iSocket = pSocket;
        iResources = pResources;

        try {
            iInStream = new ObjectInputStream(iSocket.getInputStream());
            iThread = new Thread(this);
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB MasterServerController's constructor: ");
            err.printStackTrace();
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
    }

    private void handleClient(Package pBox) {
        try {
            ObjectOutputStream shipper = new ObjectOutputStream(iSocket.getOutputStream());
            Package pkg = new Package(MasterServer.LABEL, "", iResources);
            shipper.writeObject(pkg);
            shipper.close();
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.run(): ");
            err.printStackTrace();
        }
    }

    public void run() {
        try {
            Package box = (Package) iInStream.readObject();

            if (box.getiService().equals(FileServer.LABEL)) {
                if (box.getiMessage().equals("CONNECT")) {
                    handleFilesServer(box);
                } else if (box.getiMessage().equals("CLOSE")) {
                    HostInfo file_server_host = (HostInfo) box.getiContent();
                    for (int i = 0; i < iResources.size(); ++i) {
                        if (iResources.get(i).getiFileServer().getiAddress().equals(file_server_host.getiAddress())) {
                            iResources.remove(i);
                            break;
                        }
                    }
                }
            } else if (box.getiService().equals(Client.LABEL)) {
                handleClient(box);
            }

            iSocket.close();
        } catch (IOException | ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB MasterServerController.run(): ");
            err.printStackTrace();
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
    private static ArrayList<FileContainer> iResources;

    public MasterServer() {
        if (iThread == null) {
            iThread = new Thread(this);
        }

        iLocal = Utils.loadHostInfo("./config/master.txt");
        iResources = new ArrayList<>();
    }

    public ArrayList<FileContainer> getiResources() {
        return iResources;
    }

    public void run() {
        startServer();
        while (iMaster != null) { // MASTER-SERVER khởi chạy thành công
            try {
                Socket socket = iMaster.accept();
                MasterServerController handler = new MasterServerController(socket, iResources);
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
        }
    }
}
