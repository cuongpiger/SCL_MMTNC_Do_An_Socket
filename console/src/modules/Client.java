package modules;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/*
 * Xử lí một file duy nhất mà Client cần download
 * */
class ClientController implements Runnable {
    private static HostInfo iFileServerHost = null; // HostInfo của File Server lưu file cần tải
    private Thread iThread = null;
    private String iFilename = null; // tên file cần tải
    private DatagramSocket iSocket = null;
    private InetAddress iFileServer = null; // object tham chiếu đến File Server
    private DatagramPacket iInPacket = null; // gói tin vào
    private DatagramPacket iOutPacket = null; // gói tin ra
    private byte[] iBuffer = null; // buffer đóng gói dữ liệu khi convert sang byte array

    public ClientController(HostInfo pFileServerHost, String pFilename) {
        iFileServerHost = pFileServerHost;
        iFilename = pFilename;

        try {
            iFileServer = InetAddress.getByName(iFileServerHost.getiAddress());
        } catch (UnknownHostException err) {
            iFileServer = null;
        }
    }

    /*
     * Dùng để chuẩn bị các thông tin cần thiết về file mà Client muốn tải
     * */
    private byte[] prepareOrder() {
        try {
            Package order = new Package(Client.LABEL, "DOWNLOADED-FILE", iFilename);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(baos);
            oo.writeObject(order);
            oo.close();

            byte[] box = baos.toByteArray(); // chuyển sang byte array
            return box;
        } catch (IOException err) {
            return null;
        }
    }

    public void run() {
        try {
            if (iSocket == null) iSocket = new DatagramSocket();

            iBuffer = prepareOrder(); // chuẩn bị các thông tin về file cần tải
            File received_file = null;
            FileInfo file_info = null;
            String file_name = null;

            if (iBuffer != null && iFileServer != null) {
                iOutPacket = new DatagramPacket(iBuffer, iBuffer.length, iFileServer, iFileServerHost.getiPort());
                iSocket.send(iOutPacket); // gửi đi

                iBuffer = new byte[FileServerController.PIECE];
                iInPacket = new DatagramPacket(iBuffer, iBuffer.length);
                iSocket.receive(iInPacket); // nhận file info về

                // giải nén và đọc file, chuẩn file ra đối tượng FileInfo
                ByteArrayInputStream bais = new ByteArrayInputStream(iInPacket.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                file_info = (FileInfo) ois.readObject();

                // lưu file vào đường dẫn này
                file_name = "./downloads/" + Utils.getCurrentTimestamp() + file_info.getiFileDetails().getiName();
                received_file = new File(file_name);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(received_file));

                // System.out.println("Num partition: " + file_info.getiNoPartitions());
                for (int i = 0; i < (file_info.getiNoPartitions() - 1); ++i) {
                    iInPacket = new DatagramPacket(iBuffer, iBuffer.length);

                    // nhận file từ đây
                    while (true) {
                        try {
                            iSocket.setSoTimeout(3000); // sau 3 giây ko nhận dc gì thì la làng lên
                            iSocket.receive(iInPacket);

                            break;
                        } catch (SocketException | SocketTimeoutException err) {
                            Package order = new Package(Client.LABEL, "RESEND-FILE", iFilename + "`" + i);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutput oo = new ObjectOutputStream(baos);
                            oo.writeObject(order);
                            oo.close();

                            byte[] box = baos.toByteArray(); // chuyển sang byte array
                            iSocket.send(new DatagramPacket(box, box.length, iFileServer, iFileServerHost.getiPort()));
                        }
                    }

                    bos.write(iBuffer, 0, FileServerController.PIECE);
                    // System.out.println("done a partition: " + (i + 1));
                }

                // viết cái byte cuối cùng
                iInPacket = new DatagramPacket(iBuffer, iBuffer.length);
                while (true) {
                    try {
                        iSocket.setSoTimeout(3000); // sau 3 giây ko nhận dc gì thì la làng lên
                        iSocket.receive(iInPacket);
                        bos.write(iBuffer, 0, file_info.getiLastByte());
                        bos.flush();
                        bos.close();
                        break;
                    } catch (SocketException | SocketTimeoutException err) {
                        Package order = new Package(Client.LABEL, "RESEND-FILE", iFilename + "`" + (file_info.getiNoPartitions() - 1));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutput oo = new ObjectOutputStream(baos);
                        oo.writeObject(order);
                        oo.close();

                        byte[] box = baos.toByteArray(); // chuyển sang byte array
                        iSocket.send(new DatagramPacket(box, box.length, iFileServer, iFileServerHost.getiPort()));
                    }
                }
                Client.reduceiNoProcess(); // giảm một phiên download cho client
                // System.out.println(">> Download done");
            }
            iSocket.close();
        } catch (IOException | ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB ClientController.run(): ");
            err.printStackTrace();
            iSocket = null;
        }
    }

    public void startThread() {
        iThread = new Thread(this);
        iThread.start();
    }
}


public class Client implements Runnable {
    public static final String LABEL = "CLIENT";
    private static HostInfo iMaster = null;
    private static Thread iThread = null;
    private static ArrayList<FileContainer> iResource = null;
    private static int iNoPrecess = 0;

    public Client() {
        iMaster = Utils.loadHostInfo("./config/master.txt");
        iResource = new ArrayList<>();
    }

    public ArrayList<FileContainer> getiResource() {
        return iResource;
    }

    public boolean canDownload() {
        if (iNoPrecess < 3 && iNoPrecess >= 0) {
            iNoPrecess += 1;

            return true;
        }

        return false;
    }

    public static void reduceiNoProcess() {
        if (iNoPrecess >= 1) iNoPrecess -= 1;
    }

    public void run() {
        getFiles();
    }

    public void startClient(String pCommand) {
        if (pCommand.equals("REFRESH") && iThread == null) {
            iThread = new Thread(this);
            iThread.start();
        }
    }

    public void download(String pFileName, HostInfo pFileServer) {
        ClientController downloader = new ClientController(pFileServer, pFileName);
        downloader.startThread();
    }

    private void getFiles() {
        try {
            Socket master = new Socket(iMaster.getiAddress(), iMaster.getiPort());
            ObjectOutputStream bird = new ObjectOutputStream(master.getOutputStream());
            Package order = new Package(LABEL, "", null);
            bird.writeObject(order);
            ObjectInputStream shipper = new ObjectInputStream(master.getInputStream());
            Package response = (Package) shipper.readObject();
            iResource = (ArrayList<FileContainer>) response.getiContent();
            bird.close();
            shipper.close();
            master.close();
            iThread = null;

            return;
        } catch (UnknownHostException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
        } catch (NullPointerException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
        } catch (ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
        }

        iThread = null;
    }
}
