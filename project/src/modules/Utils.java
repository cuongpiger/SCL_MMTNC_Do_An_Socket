package modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utils {
    public static HostInfo loadHostInfo(String pPath) {
        HostInfo hi = new HostInfo();

        try {
            Scanner reader = new Scanner(new File(pPath));
            String[] line = null;

            while (reader.hasNext()) {
                line = reader.nextLine().split("=");

                if (line[0].equals("ADDRESS")) {
                    hi.setiAddress(line[1]);
                } else {
                    hi.setiPort(Integer.parseInt(line[1]));
                }
            }

        } catch (FileNotFoundException err) {
            return null;
        }

        return hi;
    }

    public static String getCurrentTimestamp() {
        return Long.toString(System.currentTimeMillis());
    }

    public static FileDetails getFileDetails(File pFile) {
        String name = pFile.getName(); // tên file
        long size = pFile.length(); // size của file

        return new FileDetails(name, size);
    }

    public static ArrayList<FileDetails> loadResources(String pPath) {
        File resources = new File(pPath);
        File[] list_files = resources.listFiles();
        ArrayList<FileDetails> files = new ArrayList<>();

        if (list_files != null) {
            for (File f : list_files) {
                FileDetails new_file = getFileDetails(f);
                files.add(new_file);
            }
        }

        return files;
    }
}
