package modules;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FileServer {
    private static HostInfo iLocal;
    private static HostInfo iMaster;
    private static FileServerController iController;
    private static ArrayList<FileDetails> iFiles;

    public FileServer() {
        iLocal = Utils.loadHostInfo("./config/local.txt"); // load host info của file server
        iMaster = Utils.loadHostInfo("./config/master.txt"); // load host info của master server
        iFiles = Utils.loadResources("./resources/"); // load file detail từ folder resources

        System.out.println(">> FileServer constructor: " + iFiles.get(0).getiSize());
    }

    public HostInfo getiLocal() {
        return iLocal;
    }

    public ArrayList<FileDetails> getiFiles() {
        return iFiles;
    }

    /*
    * Gửi đến master server thông tin các file mà file server đang có
    * */
    public void talkMaster() {
        Socket master = null;

        try {
            master = new Socket(iMaster.getiAddress(), iMaster.getiPort());
            ObjectOutputStream out_stream = new ObjectOutputStream(master.getOutputStream());
            FileContainer container = new FileContainer(iLocal, iFiles);
        } catch (UnknownHostException err) {

        } catch (IOException err) {

        }
    }
}
