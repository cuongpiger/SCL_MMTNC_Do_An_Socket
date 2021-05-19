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

    private void connectFileServer() {
        try {
            iSocket = new DatagramSocket(4321);
        } catch (SocketException err) {
            System.out.print("\uD83D\uDEAB ClientController.connectFileServer(): ");
            err.printStackTrace();

            iUI.showDialog("An error occurred while getting the resource from MASTER-SERVER!");
            iSocket = null;
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

    private FileInfo receiveFileInfo() {
        try {
            iBuffer = new byte[FileServerController.PIECE];
            iInPacket = new DatagramPacket(iBuffer, iBuffer.length);
            iSocket.receive(iInPacket);
            System.out.println(">>>>>");
            ByteArrayInputStream bais = new ByteArrayInputStream(iInPacket.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);
            FileInfo file_info = (FileInfo) ois.readObject();

            System.out.println("run here");

            return file_info;
        } catch (IOException | ClassNotFoundException err) {
            return null;
        }
    }

    public void run() {
        int current_state = 0;
        connectFileServer();
        while (iSocket != null) {
            iBuffer = prepareOrder();

            if (iBuffer != null && iFileServer != null) {
                try {
                    iOutPacket = new DatagramPacket(iBuffer, iBuffer.length, iFileServer, iFileServerHost.getiPort());
                    iSocket.send(iOutPacket);

                    iBuffer = new byte[FileServerController.PIECE];
                    iInPacket = new DatagramPacket(iBuffer, iBuffer.length);
                    iSocket.receive(iInPacket);
                    System.out.println(">>>>>");
                    ByteArrayInputStream bais = new ByteArrayInputStream(iInPacket.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    FileInfo file_info = (FileInfo) ois.readObject();

                    System.out.println("run here");

                    System.out.println("send file thành công");
                    current_state = 1;
                } catch (IOException | ClassNotFoundException err) {
                    err.printStackTrace();
                    current_state = 0;
                }
            }

//            if (current_state == 1) { // gửi thông tin file cần tải đến cho file server thành công
//                FileInfo file_info = receiveFileInfo(); // nhận thông tin file cần tải
//
//                if (file_info != null) {
//                    System.out.println(">> " + file_info.getiFileDetails().getiName());
//                }
//            }
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
