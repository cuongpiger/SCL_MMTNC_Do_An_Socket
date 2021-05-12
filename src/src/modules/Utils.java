package modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {
    public static HostInfo getHostInfo(String path) {
        String hostid = "";
        int port = -1;
        String[] line = null;

        try {
            Scanner reader = new Scanner(new File(path));

            while (reader.hasNext()) {
                line = reader.nextLine().split("=");

                if (line[0].equals("host")) hostid = line[1];
                else port = Integer.parseInt(line[1]);
            }
        } catch (FileNotFoundException err) {
            return null;
        }

        return new HostInfo(hostid, port);
    }

    public static FileDetails getFileDetails(File file) {
        String file_name = file.getName();
        long size = file.length();
        String path = FileServerHandler.RESOURCES + "/" + file_name;

        return new FileDetails(path, file_name, size);
    }

    public static ArrayList<FileDetails> loadResources(String path) {
        File resource_folder = new File(path);
        File[] lst_files = resource_folder.listFiles();
        ArrayList<FileDetails> files = new ArrayList<>();

        if (lst_files != null) {
            System.out.println("go here");

            for (File f : lst_files) {
                FileDetails new_file = Utils.getFileDetails(f);
                files.add(new_file);
            }
        }

        return files;
    }
}
