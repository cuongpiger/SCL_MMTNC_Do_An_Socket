import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.io.*;

public class FileServer {
    private static ArrayList<FileInfo> files = null;
    private static final String resources = "./data";
    private static InetAddress master_host;
    private static final int master_port = 1234;
    private static ObjectOutputStream out_stream;

    private void loadResources() {
        File resource_folder = new File(resources);
        File[] lst_files = resource_folder.listFiles();

        if (lst_files != null) {
            for (File f : lst_files) {
                try {
                    FileInfo new_file = getFileInfo(f);
                    files.add(new_file);
                } catch (IOException err) {
                    continue;
                }
            }
        }
    }

    private FileInfo getFileInfo(File file) throws IOException {
        String file_name = file.getName();
        long size = file.length();
        String path = resources + "/" + file_name;

        return new FileInfo(path, file_name, size);
    }

    public void printFiles() {
        for (var file : files) {
            System.out.println(">> " + file.getFile_name() + " | " + file.getSizeFormat());
        }
    }

    public static void talkToMasterServer() {
        Socket master_server = null;

        try {
            master_server = new Socket(master_host, master_port);
            out_stream = new ObjectOutputStream(master_server.getOutputStream());
            FileContainer message = new FileContainer("rosie", 4567, files);
            out_stream.writeObject(message);
            out_stream.close();
            master_server.close();
        } catch (IOException err) {
            err.printStackTrace();
            System.exit(1);
        }
    }

    FileServer() {
        files = new ArrayList<>();
    }

    public static void main(String[] args) {
        FileServer file_server = new FileServer();
        file_server.loadResources();
        System.out.println(">> Reading file completed");
        file_server.printFiles();
        System.out.println(">> Send file");

        try {
            master_host = InetAddress.getLocalHost();
        } catch (UnknownHostException err) {
            System.out.println("==> Host ID not found");
            System.exit(1);
        }

        talkToMasterServer();
    }
}
