import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer {
    private static final int PIECE_OF_FILE = 1024*32;
    private DatagramSocket iServer;
    private int iPort = 1234;

    private void openServer() {
        try {
            iServer = new DatagramSocket(iPort);
            System.out.println("Server is running on port " + iPort);
            listening();
        } catch (SocketException err) {
            err.printStackTrace();
        }
    }

    private void listening() {
        while (true) {
            receiveFile();
        }
    }

    public void receiveFile() {
        byte[] received_data = new byte[PIECE_OF_FILE];
        DatagramPacket received_package;

        try {
            received_package = new DatagramPacket(received_data, received_data.length);
            iServer.receive(received_package);
            InetAddress inet_address = received_package.getAddress();
            ByteArrayInputStream bais = new ByteArrayInputStream(received_package.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);
            FileImage file_image = (FileImage) ois.readObject();

            // show file info
            if (file_image != null) {
                System.out.println("File name: " + file_image.getiFilename());
                System.out.println("File size: " + file_image.getiFilesize());
                System.out.println("Pieces of file: " + file_image.getiPieces());
                System.out.println("Last byte length: " + file_image.getiPreviousByteLength());
            }

            System.out.println("Receiving file...");
            File file_receive = new File(file_image.getiDestination() + file_image.getiFilename());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file_receive));
            // write pieces of file
            for (int i = 0; i < (file_image.getiPieces() - 1); ++i) {
                received_package = new DatagramPacket(received_data, received_data.length, inet_address, iPort);
                iServer.receive(received_package);
                bos.write(received_data, 0, PIECE_OF_FILE);
            }

            // write last bytes of file
            received_package = new DatagramPacket(received_data, received_data.length, inet_address, iPort);
            iServer.receive(received_package);
            bos.write(received_data, 0, file_image.getiPreviousByteLength());
            bos.flush();
            System.out.println("Done");
            bos.close();
        } catch (IOException err) {
            err.printStackTrace();
        } catch (ClassNotFoundException err) {
            err.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        server.openServer();
    }
}
