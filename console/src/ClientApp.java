import modules.Client;

import java.util.Scanner;

public class ClientApp {
    private static Client iHandler = null;

    public static void showFiles() {
        System.out.format("%5s%40s%20s%30s\n", "#ID", "Filename", "Size", "Address File-Server");
        var containers = iHandler.getiResource();

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

    public static void download(int id) {
        var containers = iHandler.getiResource();

        int i = 1;
        for (var container : containers) {
            var host = container.getiFileServer();
            var files = container.getiFiles();

            for (var file : files) {
                if (id == i) {
                    if (iHandler != null && iHandler.canDownload()) {
                        iHandler.download(file.getiName(), host);
                        return;
                    }
                }

                i += 1;
            }
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String[] menu = {
                "\uD83D\uDCBB  CLIENT",
                "1. DOWNLOAD",
                "2. CLOSE"
        };

        while (true) {
            System.out.println("\n_____________________________________________________________________________\n");
            for (String line : menu) System.out.println(line);
            System.out.print("Enter your choice: ");
            int cmd = input.nextInt();

            if (cmd == 1) {
                if (iHandler == null) {
                    iHandler = new Client();
                }
                iHandler.startClient("REFRESH");
                // showFiles();
                System.out.print("\nEnter file's ID: ");
                int id = input.nextInt();
                download(id);

            } else if (cmd == 2) {
                System.exit(0);
            }
        }
    }
}