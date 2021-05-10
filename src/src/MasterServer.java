import java.io.*;
import java.net.*;
import java.util.*;

class FileServerHandler extends Thread {
    private Socket file_server;
    private ObjectInputStream in_stream;
    private PrintWriter out_stream;

    public FileServerHandler(Socket fs) {
        file_server = fs;

        try {
            in_stream = new ObjectInputStream(file_server.getInputStream()); // đọc các object dc gửi từ phía file server
        } catch (IOException err) {
            err.printStackTrace();
            System.exit(1);
        }
    }

    public void run() {
        try {
            FileContainer received = (FileContainer) in_stream.readObject();

            for (var file : received.getFiles()) {
                System.out.println(">> " + file.getFile_name() + " | " + file.getSizeFormat());
            }

            file_server.close();

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

            FileServerHandler handler = new FileServerHandler(client);
            handler.start();
        } while (true);
    }

}
