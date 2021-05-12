import java.io.*;
import java.net.*;
import java.util.*;

class MasterServerHandler extends Thread {
    private Socket secretary ; // nhận các `Package` từ `FileServer` và `lient`
    private ObjectInputStream in_stream;
    private PrintWriter out_stream;

    public MasterServerHandler(Socket fs) {
       secretary  = fs;

        try {
            in_stream = new ObjectInputStream(secretary.getInputStream()); // đọc các object dc gửi từ phía file server
        } catch (IOException err) {
            err.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        try {
            Package pkg = (Package) in_stream.readObject();

            if (pkg.getService().equals(FileServerHandler.LABEL)) {
                FileContainer content = (FileContainer) pkg.getContent();

                for (var file : content.getFiles()) {
                    System.out.println(">> " + file.getFile_name() + " | " + file.getSizeFormat());
                }
            }

            secretary.close();

        } catch (ClassNotFoundException | IOException err) {
            err.printStackTrace();
            System.exit(1);
        }
    }
}

public class MasterServer {
    private static ServerSocket master_server;
    private static final int PORT = 1234;

    public static void main(String[] args) throws IOException {
        try {
            master_server = new ServerSocket(PORT);
            System.out.println(">> Master-server opening on port " + PORT);
        } catch (IOException err) {
            System.out.println("==> Unable to set up port " + PORT);
            System.exit(1);
        }

        do {
            Socket client = master_server.accept();
            System.out.println(">> New client accepted");

            MasterServerHandler handler = new MasterServerHandler(client);
            handler.start();
        } while (true);
    }

}
