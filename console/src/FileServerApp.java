import modules.FileDetails;
import modules.FileServer;
import java.util.ArrayList;
import java.util.Scanner;

public class FileServerApp {
    private static FileServer iHandler = null;
    private static ArrayList<FileDetails> iFiles = new ArrayList<>();

    private static void updateiFiles(ArrayList<FileDetails> pFiles) {
        iFiles.addAll(pFiles);
    }

    public static void showiFiles() {
        System.out.format("%5s%40s%20s\n", "#ID", "Filename", "Size");
        for (int i = 0; i < iFiles.size(); ++i) {
            var tmp = iFiles.get(i);
            System.out.format("%5d%40s%20s\n", i + 1, tmp.getiName(), tmp.getiSize() + " bytes");
        }
    }

    public static void sendFiles() {
        iHandler = new FileServer();
        ArrayList<FileDetails> files = iHandler.getiFiles();
        updateiFiles(files);
        iHandler.start("SEND-FILES-TO-MASTER");
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String[] menu = {
                "\uD83D\uDDC3  FILES-SERVER",
                "1. Send files' information to MASTER-SERVER",
                "2. START",
                "3. List all available files",
                "4. CLOSE"
        };

        while (true) {
            System.out.println("\n_____________________________________________________________________________\n");
            for (String line : menu) System.out.println(line);
            System.out.print("Enter your choice: ");
            int cmd = input.nextInt();

            if (cmd == 1) {
                sendFiles();
            } else if (cmd == 2) {
                if (iHandler != null) {
                    iHandler.start("START-FILE-SERVER");
                }
            } else if (cmd == 3) {
                showiFiles();
            } else if (cmd == 4) {
                iHandler = new FileServer();
                iHandler.start("CLOSE-FILE-SERVER");
            }
        }
    }
}
