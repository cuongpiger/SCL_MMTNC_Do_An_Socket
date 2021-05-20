import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class FileDownloader extends Thread {
    private DatagramSocket datagram_socket = null;
    private InetSocketAddress file_server_host = null;

    /*
     * Constructor
     * > Parameters:
     *   > pHostFileServer: hostname của `FileServer`
     *   > pPort: port của `FileServer` đang mở
     * */
    FileDownloader(String pHostFileServer, int pPort) throws IOException {
        datagram_socket = new DatagramSocket();
        file_server_host = new InetSocketAddress(pHostFileServer, pPort);
    }

    /*
     * Gửi đến cho `FileServer` thông tin về file cần tải về
     * > Parameters:
     *   > pFile: thông tin file mà `Client` cần tải về
     * */
    private void sendDownloadedFileInfo(FileInfo pFile) throws IOException {
        String file_path = pFile.getPath();
        Package pkg = new Package(Client.LABEL, file_path); // `Package` gửi đến cho `FileServer` để `FileServer` biết đường dần (`file_path`) mà `Client` cần tải
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pkg);
        byte[] buffer = baos.toByteArray();;

        DatagramPacket out_packet = new DatagramPacket(buffer, buffer.length, file_server_host);
        datagram_socket.send(out_packet); // gửi thông tin file cần tải đi
    }

    /*
     * Nhận thông tin gói file sẽ tải về
     *
     * */
    private void getDownloadedFileInfo() {

    }

    /*
     * Hàm chính thực hiện toàn bộ quá trình tải file
     * */
    private void downloadFile(FileInfo pFile) throws IOException {
        try {
            sendDownloadedFileInfo(pFile);
        } catch (IOException err) {

        }
    }

}

public class Client {
    private String file_server = "192.168.1.10";
    private int file_server_port = 4321;
    private DatagramSocket datagram_socket = null;
    private DatagramPacket in_packet, out_packet;
    public static final String LABEL = "Client";


    public void sendDownloadedFileInfo(String pHostFileServer, int pPort, FileInfo pFile) throws IOException {

    }

}
