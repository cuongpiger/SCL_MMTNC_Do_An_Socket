package modules;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client implements Runnable {
    public static final String LABEL = "CLIENT";
    private static HostInfo iMaster;
    private static ArrayList<FileInfo> iFiles;
    private Thread iThread;

    public Client() {
        iMaster = Utils.loadHostInfo("./config/master.txt");
        iThread = new Thread(this);
    }

    public boolean talkMaster() {
        Socket master = null;

        try {
            master = new Socket(iMaster.getiAddress(), iMaster.getiPort());
            ObjectOutputStream bird = new ObjectOutputStream(master.getOutputStream());
            ObjectInputStream shipper = new ObjectInputStream(master.getInputStream());
            Package order = new Package(LABEL, "GET-FILES", null);
            bird.writeObject(order);
            bird.close();
            iFiles = (ArrayList<FileInfo>) shipper.readObject();


            master.close();
        } catch (UnknownHostException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
            return false;
        } catch (IOException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
            return false;
        } catch (NullPointerException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
            return false;
        } catch (ClassNotFoundException err) {
            System.out.print("\uD83D\uDEAB FileServer.talkMaster(): ");
            err.printStackTrace();
            return false;
        }

        return true;
    }

    public void run() {

    }

    public void startThread() {
        iThread.start();
    }
}
