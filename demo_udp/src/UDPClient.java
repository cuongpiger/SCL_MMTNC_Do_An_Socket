import java.io.*;
import java.net.*;

public class UDPClient {
    private static final int PIECES_FILE = 1024 * 32;
    private DatagramSocket clientSocket;
    private int serverPort = 1234;
    private String serverHost = "192.168.1.10";

    private void connectServer() {
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException err) {
            err.printStackTrace();
        }
    }

    private void senFile(String sourcePath, String destination) {
        InetAddress inetAddress;
        DatagramPacket sendPacket;

        try {
            File fileSend = new File(sourcePath);
            InputStream inputStream = new FileInputStream(fileSend);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            inetAddress = InetAddress.getByName("rosie");
            byte[] bytePart = new byte[PIECES_FILE];

            long fileLength = fileSend.length();
            int piecesOfFile = (int) (fileLength / PIECES_FILE);
            int lastByteLength = (int) (fileLength % PIECES_FILE);

            if (lastByteLength > 0) {
                piecesOfFile++;
            }

            byte[][] fileBytes = new byte[piecesOfFile][PIECES_FILE];
            int count = 0;

            while (bis.read(bytePart, 0, PIECES_FILE) > 0) {
                fileBytes[count++] = bytePart;
                bytePart = new byte[PIECES_FILE];
            }

            FileImage file_image = new FileImage();
            file_image.setiFilename(fileSend.getName());
            file_image.setiFilesize(fileSend.length());
            file_image.setiPieces(piecesOfFile);
            file_image.setiPreviousByteLength(lastByteLength);
            file_image.setiDestination(destination);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(file_image);
            sendPacket = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, inetAddress, serverPort);
            clientSocket.send(sendPacket);

            System.out.println("Sending file...");
            for (int i = 0; i < (count - 1); ++i) {
                sendPacket = new DatagramPacket(fileBytes[i], PIECES_FILE, inetAddress, serverPort);
                clientSocket.send(sendPacket);
                waitServer(40);
            }

            //send last bytes of file
            sendPacket = new DatagramPacket(fileBytes[count - 1], PIECES_FILE, inetAddress, serverPort);
            clientSocket.send(sendPacket);
            waitServer(40);
            bis.close();
        } catch (UnknownHostException err) {
            err.printStackTrace();
        } catch (IOException err) {
            err.printStackTrace();
        }

        System.out.println("Sent");
    }

    public void waitServer(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String sourcePath = "./test.mp3";
        String destination = "./test/";
        UDPClient client = new UDPClient();
        client.connectServer();
        client.senFile(sourcePath, destination);
    }
}
