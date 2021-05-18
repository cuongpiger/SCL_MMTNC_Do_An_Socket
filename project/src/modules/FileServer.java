package modules;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FileServer implements Runnable {
    public static final String LABEL = "FILE-SERVER";
    private static HostInfo iLocal;
    private static HostInfo iMaster;
    private static ArrayList<FileDetails> iFiles = null;
    private static Thread iThread = null;
    private static String iCommand = null;
    private static FileServerUI iUI = null;
    private static boolean iIsRunning = false; // FILE-SERVER đã sẵn sàng cho phép CLIENT tải file hay chưa

    public FileServer(FileServerUI pUI) {
        if (iThread == null) {
            iThread = new Thread(this);
        }

        iUI = pUI;
        iLocal = Utils.loadHostInfo("./config/file.txt"); // load host info của file server
        iMaster = Utils.loadHostInfo("./config/master.txt"); // load host info của master server
        iFiles = Utils.loadResources("./resources/"); // load file details từ folder resources
    }

    public HostInfo getiLocal() {
        return iLocal;
    }

    public ArrayList<FileDetails> getiFiles() {
        return iFiles;
    }

    public boolean getiABoolean() {
        return iIsRunning;
    }

    /*
     * Gửi đến MASTER-SERVER thông tin các file mà FILE-SERVER đang có
     * */
    private void sendFiles() {
        try {
            Socket master = new Socket(iMaster.getiAddress(), iMaster.getiPort());
            ObjectOutputStream shipper = new ObjectOutputStream(master.getOutputStream());
            FileContainer container = new FileContainer(iLocal, iFiles);
            Package box = new Package(LABEL, "New file-server is connecting", container);
            shipper.writeObject(box);
            master.close();

            return;
        } catch (UnknownHostException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
        } catch (NullPointerException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
        }

        iUI.showDialog(String.format("Unable to connect to MASTER-SERVER at address %s:%d", iMaster.getiAddress(), iMaster.getiPort()));
    }

    @Override
    public void run() {
        if (iCommand == null) return; // nếu ko có chỉ thị gì

        if (iCommand.equals("SEND-FILES-TO-MASTER")) { // gửi các file hiện có cho MASTER-SERVER
            sendFiles();
            iCommand = null;
        }

        if (iCommand.equals("START-FILE-SERVER")) { // chạy FILE-SERVER sẵn sàng cho các CLIENT tải tài nguyên
            // something code here
            iCommand = null;
        }
    }

    /*
    * Chạy luồng này
    * - PARAMS
    *   > pCommand: các chỉ thị lệnh do USER nhấn vào các swing component để thi hành các chức năng tương ứng
    * */
    public void startThread(String pCommand) {
        iCommand = pCommand;
        iThread.start();
    }
}
