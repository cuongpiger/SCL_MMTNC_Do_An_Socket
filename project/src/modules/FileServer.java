package modules;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FileServer {
    public static final String LABEL = "FILE-SERVER";
    private static HostInfo iLocal;
    private static HostInfo iMaster;
    private static FileServerController iController;
    private static ArrayList<FileDetails> iFiles;

    public FileServer() {
        iLocal = Utils.loadHostInfo("./config/file.txt"); // load host info của file server
        iMaster = Utils.loadHostInfo("./config/master.txt"); // load host info của master server
        iFiles = Utils.loadResources("./resources/"); // load file details từ folder resources

        talkMaster();
    }

    public HostInfo getiLocal() {
        return iLocal;
    }

    public ArrayList<FileDetails> getiFiles() {
        return iFiles;
    }

    /*
    * Gửi đến master server thông tin các file mà file server đang có
    * - RETURN: true nếu gửi đi thành công và ngược lại
    * */
    public boolean talkMaster() {
        Socket master = null;

        try {
            master = new Socket(iMaster.getiAddress(), iMaster.getiPort());
            ObjectOutputStream shipper = new ObjectOutputStream(master.getOutputStream());
            FileContainer container = new FileContainer(iLocal, iFiles);
            Package box = new Package(LABEL, "New file-server is connecting", container);
            shipper.writeObject(box);
            master.close();
        } catch (UnknownHostException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
            return false;
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
            return false;
        } catch (NullPointerException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
            return false;
        }

        return true;
    }
}
