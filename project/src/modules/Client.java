package modules;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

class ClientController implements Runnable {
    private static HostInfo iFileServerHost = null;
    private Thread iThread = null;
    private String iFilename = null;
    private DatagramSocket iSocket = null;
    private InetAddress iFileServer = null;
    private DatagramPacket iInPacket = null;
    private DatagramPacket iOutPacket = null;
    private byte[] iBuffer = null;
    private static ClientUI iUI = null;


    public ClientController(HostInfo pFileServerHost, String pFilename, ClientUI pUI) {
        iFileServerHost = pFileServerHost;
        iFilename = pFilename;
        iUI = pUI;

        try {
            iFileServer = InetAddress.getByName(iFileServerHost.getiAddress());
        } catch (UnknownHostException err) {
            iFileServer = null;
        }
    }

    private byte[] prepareOrder() {
        try {
            Package order = new Package(Client.LABEL, "DOWNLOADED-FILE", iFilename);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(baos);
            oo.writeObject(order);
            oo.close();

            byte[] box = baos.toByteArray();
            return box;
        } catch (IOException err) {
            return null;
        }
    }

    public void run() {
        try {
            iSocket = new DatagramSocket();
            iBuffer = prepareOrder(); // chuẩn bị các thông tin về file cần tải

            if (iBuffer != null && iFileServer != null) {
                iOutPacket = new DatagramPacket(iBuffer, iBuffer.length, iFileServer, iFileServerHost.getiPort());
                iSocket.send(iOutPacket); // gửi đi

                iBuffer = new byte[FileServerController.PIECE];
                iInPacket = new DatagramPacket(iBuffer, iBuffer.length);
                iSocket.receive(iInPacket); // nhận file info về

                // giải nén và đọc file, chuẩn file ra đối tượng FileInfo
                ByteArrayInputStream bais = new ByteArrayInputStream(iInPacket.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                FileInfo file_info = (FileInfo) ois.readObject();

                File received_file = new File("./downloads/" + Utils.getCurrentTimestamp() + file_info.getiFileDetails().getiName());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(received_file));


                // tải file từ đây
                int id = iUI.getRowCountDownloadTbl();
                iUI.addNewRowToDownloadTbl(file_info.getiFileDetails());
                // System.out.println("Num partition: " + file_info.getiNoPartitions());
                for (int i = 0; i < (file_info.getiNoPartitions() - 1); ++i) {
                    iInPacket = new DatagramPacket(iBuffer, iBuffer.length);
                    iSocket.receive(iInPacket);
                    bos.write(iBuffer, 0, FileServerController.PIECE);
                    // System.out.println("done a partition: " + (i + 1));
                }

                // viết cái byte cuối cùng
                iInPacket = new DatagramPacket(iBuffer, iBuffer.length);
                iSocket.receive(iInPacket);
                bos.write(iBuffer, 0, file_info.getiLastByte());
                bos.flush();
                bos.close();
                iSocket.close();
                iUI.updateDoneStatusDownloadTbl(id);
                // System.out.println(">> Download done");
            }

        } catch (IOException | ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB ClientController.run(): ");
            err.printStackTrace();

            iUI.showDialog("An error occurred while getting the resource from MASTER-SERVER!");
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
    private static ClientUI iUI = null;
    private static int iNoPrecess = 0;

    public Client(ClientUI pUI) {
        iUI = pUI;
        iMaster = Utils.loadHostInfo("./config/master.txt");
        iResource = new ArrayList<>();
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
            iUI.updateiFilesTbl(iResource);
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
        iUI.showDialog("An error occurred while getting the resource from MASTER-SERVER!");
    }
}
