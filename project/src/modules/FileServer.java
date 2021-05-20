package modules;

import java.awt.dnd.DropTarget;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

class FileServerController implements Runnable {
    public static final int PIECE = 1024 * 32; // kích thước của một part
    private static DatagramSocket iServer = null;
    private static HostInfo iHost = null;
    private static FileServerUI iUI = null;
    private static Thread iThread = null;
    private static ArrayList<FileDetails> iFiles = null;
    private static DatagramPacket iInPacket = null;

    public FileServerController(HostInfo pHost, FileServerUI pUI, ArrayList<FileDetails> pFiles) {
        iHost = pHost;
        iUI = pUI;
        iFiles = pFiles;
    }

    private void openServer() {
        try {
            iServer = new DatagramSocket(iHost.getiPort());
        } catch (SocketException err) {
            System.out.print("\uD83D\uDEAB FileServerController.openServer(): ");
            err.printStackTrace();

            iUI.showDialog("Unable to run MASTER-SERVER!");
            iServer = null;
        }
    }

    public void run() {
        iUI.updateiStatusLbl(String.format("FILE-SERVER is running on %s:%d", iHost.getiAddress(), iHost.getiPort()));
        openServer();
        while (iServer != null) {
            byte[] buffer = new byte[PIECE];

            try {
                iInPacket = new DatagramPacket(buffer, buffer.length);
                iServer.receive(iInPacket); // code từ khúc này lên trên ổn
                FileServerShipper shipper = new FileServerShipper(iServer, iInPacket, iFiles, iHost, iUI);
                shipper.startThread();
            } catch (IOException err) {
                System.out.print("\uD83D\uDEAB FileServerController.openServer(): ");
                err.printStackTrace();

                iUI.showDialog("Unable to run MASTER-SERVER!");
                iServer = null;
            }
        }
    }

    public void startThread() {
        iThread = new Thread(this);
        iThread.start();
    }
}

/*
 * Xử lí tương ứng cho một ClientController
 * */
class FileServerShipper implements Runnable {
    private DatagramPacket iInPacket = null;
    private DatagramPacket iOutPacket = null;
    private ArrayList<FileDetails> iFiles = null;
    private Thread iThread = null;
    private HostInfo iHost = null;
    private byte[] iBuffer = null;
    private DatagramSocket iSocket = null;
    private static FileServerUI iUI;

    public FileServerShipper(DatagramSocket pSocket, DatagramPacket pInPacket, ArrayList<FileDetails> pFiles, HostInfo pHost, FileServerUI pUI) {
        iSocket = pSocket;
        iInPacket = pInPacket;
        iFiles = pFiles;
        iHost = pHost;
        iUI = pUI;

        // nguyên cái constructor này ổn
    }

    public void run() {
        int current_state = 0;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(iInPacket.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Package pkg = (Package) ois.readObject();
            // khúc này lên trên ổn


            if (pkg != null && pkg.getiService().equals(Client.LABEL) && (pkg.getiMessage().equals("DOWNLOADED-FILE") || pkg.getiMessage().equals("RESEND-FILE"))) {
                String filename = null;
                int pos = -1;

                if (pkg.getiMessage().equals("DOWNLOADED-FILE")) {
                    filename = (String) pkg.getiContent();
                } else {
                    String[] tmp = ((String) pkg.getiContent()).split("`");
                    filename = tmp[0];
                    pos = Integer.parseInt(tmp[1]);
                }
                FileDetails file_details = getFileDetails(filename); // tìm gói tin
                File file_send = new File("./resources/" + file_details.getiName());
                // khúc này lên trên cũng ổn

                if (file_details != null && file_send != null) {
                    FileInfo bale = genFileInfo(file_send, file_details);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutput oo = new ObjectOutputStream(baos);
                    oo.writeObject(bale);
                    oo.close();

                    iBuffer = baos.toByteArray();
                    iOutPacket = new DatagramPacket(iBuffer, iBuffer.length, iInPacket.getAddress(), iInPacket.getPort());
                    iSocket.send(iOutPacket);

                    // chuẩn bị các đối tượng cần thiết để ghi file
                    InputStream input_stream = new FileInputStream(file_send);
                    BufferedInputStream bis = new BufferedInputStream(input_stream);

                    // gửi file đi
                    iBuffer = new byte[FileServerController.PIECE];
                    iUI.addNewRowToActivitiesTbl(iInPacket, file_details.getiName(), bale.getiNoPartitions());
                    int id = iUI.getRowCountActivitiesTbl() - 1;
                    for (int i = 0; i < (bale.getiNoPartitions() - 1); ++i) {
                        if (i < pos) {
                            continue;
                        }

                        bis.read(iBuffer, 0, FileServerController.PIECE);
                        iOutPacket = new DatagramPacket(iBuffer, iBuffer.length, iInPacket.getAddress(), iInPacket.getPort());
                        iSocket.send(iOutPacket);
                        waitClient(200);

                        if (i % 10 == 0) {
                            iUI.updateStateActivitiesTbl(id, String.format("sending %d/%d partitions", i + 1, bale.getiNoPartitions()));
                        }
                    }

                    // gửi những byte cuối cùng
                    bis.read(iBuffer, 0, bale.getiLastByte());
                    iOutPacket = new DatagramPacket(iBuffer, iBuffer.length, iInPacket.getAddress(), iInPacket.getPort());
                    iSocket.send(iOutPacket);
                    bis.close();
                    iUI.updateStateActivitiesTbl(id, "Done");
                }
            }
        } catch (IOException | ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB FileServerShipper.run(): ");
            err.printStackTrace();

            iUI.showDialog("An error occurred while running FILE-SERVER!");
        }
    }

    private void waitClient(long iMiliseconds) {
        try {
            Thread.sleep(iMiliseconds);
        } catch (InterruptedException err) {
            return;
        }
    }

    public void startThread() {
        iThread = new Thread(this);
        iThread.start();
    }

    // trả về FileDetails dựa vào filename
    private FileDetails getFileDetails(String pFile) {
        for (var file : iFiles) {
            if (file.getiName().equals(pFile)) {
                return file;
            }
        }

        return null;
    }

    private FileInfo genFileInfo(File pFile, FileDetails pFileDetails) {
        long file_length = pFile.length();
        int last_byte = (int) (file_length % FileServerController.PIECE);
        int no_parts = (int) (file_length / FileServerController.PIECE) + (last_byte > 0 ? 1 : 0);

        return new FileInfo(pFileDetails, no_parts, last_byte, FileInfo.genSha256(pFile));
    }
}

class FileServerCloser implements Runnable {
    private static Thread iThread = null;
    private static HostInfo iMaster = null;
    private static HostInfo iLocal = null;
    private static FileServerUI iUI = null;

    public FileServerCloser(HostInfo pMaster, HostInfo pLocal, FileServerUI pUI) {
        iMaster = pMaster;
        iLocal = pLocal;
        iUI = pUI;
    }

    public void run() {
        try {
            Socket master = new Socket(iMaster.getiAddress(), iMaster.getiPort());
            ObjectOutputStream shipper = new ObjectOutputStream(master.getOutputStream());
            Package box = new Package(FileServer.LABEL, "CLOSE", iLocal);
            shipper.writeObject(box);
            master.close();
            System.exit(0);
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB FileServerCloser.run(): ");
            err.printStackTrace();

            iUI.showDialog("An error occurred while closing FILE-SERVER!");
        }
    }

    public void startThread() {
        iThread = new Thread(this);
        iThread.start();
    }
}

public class FileServer implements Runnable {
    public static final String LABEL = "FILE-SERVER";
    private static HostInfo iLocal; // HostInfo của File Server
    private static HostInfo iMaster; // HostInfo của máy Master
    private static ArrayList<FileDetails> iFiles = null; // chứa danh sách các file mà File Server này đang giữ
    private static Thread iThread = null;
    private static FileServerUI iUI = null; // tham chiếu đến giao diện

    public FileServer(FileServerUI pUI) {
        if (iThread == null) {
            iThread = new Thread(this);
        }

        iUI = pUI;
        iLocal = Utils.loadHostInfo("./config/file.txt"); // load host info của file server
        iMaster = Utils.loadHostInfo("./config/master.txt"); // load host info của master server
        iFiles = Utils.loadResources("./resources/"); // load file details từ folder resources
    }

    public HostInfo getiLocal() {
        return iLocal;
    }

    public ArrayList<FileDetails> getiFiles() {
        return iFiles;
    }

    /*
     * Gửi đến MASTER-SERVER thông tin các file mà FILE-SERVER đang có
     * */
    private void sendFiles() {
        try {
            Socket master = new Socket(iMaster.getiAddress(), iMaster.getiPort());
            ObjectOutputStream shipper = new ObjectOutputStream(master.getOutputStream());
            FileContainer container = new FileContainer(iLocal, iFiles);
            Package box = new Package(LABEL, "CONNECT", container);
            shipper.writeObject(box);
            master.close();
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
        }

        iUI.showDialog(String.format("Unable to connect to MASTER-SERVER at address %s:%d", iMaster.getiAddress(), iMaster.getiPort()));
    }

    @Override
    public void run() {
        sendFiles();
        iThread = null;
    }

    /*
     * Chạy luồng này
     * - PARAMS
     *   > pCommand: các chỉ thị lệnh do USER nhấn vào các swing component để thi hành các chức năng tương ứng
     * */
    public void start(String pCommand) {
        if (pCommand.equals("SEND-FILES-TO-MASTER")) {
            if (iThread == null) iThread = new Thread(this);

            iThread.start();
        } else if (pCommand.equals("START-FILE-SERVER")) {
            FileServerController controller = new FileServerController(iLocal, iUI, iFiles);
            controller.startThread();
        } else if (pCommand.equals("CLOSE-FILE-SERVER")) {
            FileServerCloser closer = new FileServerCloser(iMaster, iLocal, iUI);
            closer.startThread();
        }
    }
}
