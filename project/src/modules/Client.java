package modules;

import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
    public static final String LABEL = "CLIENT";
    private static HostInfo iMaster;
    private static ArrayList<FileInfo> iFiles;
    protected static DefaultTableModel iDownloadEditor;

    public Client() {
        iMaster = Utils.loadHostInfo("./config/master.txt");
    }

    public void addRowFilesTbl(DefaultTableModel pFilesEditor) {
        for (int i = 0; i < iFiles.size(); ++i) {
            var host = iFiles.get(i).getiHost();
            var file = iFiles.get(i).getiFile();

            pFilesEditor.addRow(new Object[] {
                    Integer.toString(i + 1),
                    file.getiName(),
                    file.getiSize() + " bytes",
                    String.format("%s:%d", host.getiAddress(), host.getiPort())
            });
        }
    }

    public boolean talkMaster(DefaultTableModel pFilesEditor, DefaultTableModel pDownloadEditor) {
        Socket master = null;

        try {
            master = new Socket(iMaster.getiAddress(), iMaster.getiPort());
            ObjectOutputStream bird = new ObjectOutputStream(master.getOutputStream());
            Package order = new Package(LABEL, "GET-FILES", null);
            bird.writeObject(order);
            ObjectInputStream shipper = new ObjectInputStream(master.getInputStream());
            Package response = (Package) shipper.readObject();
            iFiles = (ArrayList<FileInfo>) response.getiContent();
            System.out.print(iFiles.size());
            bird.close();
            shipper.close();
            master.close();

            addRowFilesTbl(pFilesEditor);
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
}
