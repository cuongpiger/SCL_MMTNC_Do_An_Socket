package modules;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.*;

//class ClientHandler extends Thread {
//    private InetSocketAddress client_host = null;
//
//    private void getDownloadedFileInfo() {
//
//    }
//}

public class FileServerHandler {
    private ArrayList<FileDetails> files = null; // lưu danh sách các file
    public static final String LABEL = "FileServer";
    public static final String RESOURCES = "./file_server/data";

    public FileServerHandler(ArrayList<FileDetails> pFiles) {
        files = pFiles;
    }

    public ArrayList<FileDetails> getFiles() {
        return files;
    }





//    public void printFiles() {
//        for (var file : files) {
//            System.out.println(">> " + file.getFile_name() + " | " + file.getSizeFormat());
//        }
//    }
//
//    public static void talkToMasterServer() {
//        Socket master_server = null;
//
//        try {
//            master_server = new Socket(master_host, master_port);
//            out_stream = new ObjectOutputStream(master_server.getOutputStream());
//            FileContainer message = new FileContainer("rosie", 4567, files);
//            Package pkg = new Package(LABEL, message);
//            out_stream.writeObject(pkg);
//            out_stream.close();
//            master_server.close();
//        } catch (IOException err) {
//            err.printStackTrace();
//            System.exit(1);
//        }
//    }

//    FileServer() {
//        files = new ArrayList<>();
//    }

    //////////////////////////////////// CLIENT ////////////////////////////////////








    ////////////////////////////////////////////////////////////////////////////////

//    public static void main(String[] args) {
//        FileServer file_server = new FileServer();
//        file_server.loadResources();
//        System.out.println(">> Reading file completed");
//        file_server.printFiles();
//        System.out.println(">> Send file");
//
//        try {
//            master_host = InetAddress.getLocalHost();
//        } catch (UnknownHostException err) {
//            System.out.println("==> Host ID not found");
//            System.exit(1);
//        }
//
//        talkToMasterServer();
//    }
}
