import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

//class ClientHandler extends Thread {
//    private InetSocketAddress client_host = null;
//
//    private void getDownloadedFileInfo() {
//
//    }
//}

public class FileServerHandler {
    private static ArrayList<FileDetails> files = null; // lưu danh sách các file
    private static HostInfo master = null;
    private static HostInfo host = null;
    private static ObjectOutputStream out_stream;
    public static final String LABEL = "FileServer";
    public static final String RESOURCES = "./file_server/data";

    public FileServerHandler(HostInfo pHost, HostInfo pMaster, ArrayList<FileDetails> pFiles) {
        files = pFiles;
        master = pMaster;
        host = pHost;
    }

    public ArrayList<FileDetails> getFiles() {
        return files;
    }

    public void talkToMasterServer() {
        Socket master_server = null;

        try {
            master_server = new Socket(master.getHostname(), master.getPort());
            out_stream = new ObjectOutputStream(master_server.getOutputStream());
            FileContainer message = new FileContainer(master.getHostname(), master.getPort(), files);
            Package pkg = new Package(LABEL, String.format("%s:%d", host.getHostname(), host.getPort()), message);
            out_stream.writeObject(pkg);
            out_stream.close();
            master_server.close();
        } catch (UnknownHostException err) {
            err.printStackTrace();
            System.exit(1);
        } catch (IOException err) {
            err.printStackTrace();
            System.exit(1);
        }
    }
}
