import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

class MasterServerTrigger implements Runnable {
    Socket iSocket;
    ObjectInputStream iInStream;
    Thread iThread;

    MasterServerTrigger(Socket pSocket) {
        iSocket = pSocket;

        try {
            iInStream = new ObjectInputStream(iSocket.getInputStream());
            iThread = new Thread(this);
        } catch (IOException err) {
            err.printStackTrace();
            System.exit(1);
        }
    }

    public void runThread() {
        iThread.start();
    }

    public void run() {
        try {
            Package pkg = (Package) iInStream.readObject();

            if (pkg.getiService().equals(FileServerHandler.LABEL)) {
                FileContainer content = (FileContainer) pkg.getiContent();

                for (var file : content.getFiles()) {
                    MasterServerUI.eTable.addRow(new Object[] {++MasterServerHandler.iNoFiles, file.getFile_name(), file.getSizeFormat(), pkg.getiMessage()});
                }
            }

            iSocket.close();
        } catch (IOException | ClassNotFoundException err) {
            err.printStackTrace();
            System.exit(1);
        }
    }
}

/*
* Dùng để xử lí các request từ phía `FileServer` và `Client`
* */
public class MasterServerHandler implements Runnable {
    private static ServerSocket iMasterServer;
    private static final int iPORT = 1234;
    private static Thread iThread = null;
    public static int iNoFiles = 0;

    public MasterServerHandler() {
        iThread = new Thread(this);
    }

    public ServerSocket getiMasterServer() {
        return iMasterServer;
    }

    public int getiPORT() {
        return iPORT;
    }

    public void runThread() {
        iThread.start();
    }

    public void run() {
        try {
            iMasterServer = new ServerSocket(iPORT);
        } catch (IOException err) {
            err.printStackTrace();
            System.exit(1);
        }

        // Nhận các request từ cả hai phía `Client` và `FileServer`
        while (true) {
            try {
                Socket socket = iMasterServer.accept();
                System.out.println(">> New client accepted");

                MasterServerTrigger handler = new MasterServerTrigger(socket);
                handler.runThread();
            } catch (IOException err) {
                err.printStackTrace();
                System.exit(1);
            }
        }
    }
}
