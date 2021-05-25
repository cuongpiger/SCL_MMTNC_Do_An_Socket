import modules.MasterServer;

import java.util.Scanner;

public class MasterServerApp {
    private static MasterServer iHandler = null;

    public static void showFiles() {
        System.out.format("%5s%40s%20s%30s\n", "#ID", "Filename", "Size", "Address File-Server");
        var containers = iHandler.getiResources();

        int i = 1;
        for (var container : containers) {
            var host = container.getiFileServer();
            var host_str = String.format("%s:%d", host.getiAddress(), host.getiPort());
            var files = container.getiFiles();

            for (var file : files) {
                System.out.format("%5d%40s%20s%30s\n", i++, file.getiName(), file.getiSize() + " bytes", host_str);
            }
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String[] menu = {
                "\uD83D\uDDA5  MASTER-SERVER",
                "1. START",
                "2. List all available files",
                "3. CLOSE"
        };

        while (true) {
            System.out.println("\n_____________________________________________________________________________\n");
            for (String line : menu) System.out.println(line);
            System.out.print("Enter your choice: ");
            int cmd = input.nextInt();

            if (cmd == 1) {
                if (iHandler == null) {
                    iHandler = new MasterServer();
                    iHandler.startThread();
                }
            } else if (cmd == 2) {
                showFiles();
            } else if (cmd == 3) {
                System.exit(0);
            }
        }
    }
}
