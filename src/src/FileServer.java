import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.*;

public class FileServer {
    private static ArrayList<FileInfo> files = null;
    private static final String resources = "./data";

    private void loadResources() {
        File resource_folder = new File(resources);
        File[] lst_files = resource_folder.listFiles();

        if (lst_files != null) {
            for (File f : lst_files) {
                try {
                    FileInfo new_file = getFileInfo(f);
                    files.add(new_file);
                } catch (IOException err) {
                    continue;
                }
            }
        }
    }

    private FileInfo getFileInfo(File file) throws IOException {
        String file_name = file.getName();
        long size = file.length();
        String path = resources + "/" + file_name;

        return new FileInfo(path, file_name, size);
    }

    public void printFiles() {
        for (var file : files) {
            System.out.println(">> " + file.getFile_name() + " | " + file.getSizeFormat());
        }
    }

    FileServer() {
        files = new ArrayList<>();
    }

    public static void main(String[] args) {
        FileServer file_server = new FileServer();
        file_server.loadResources();
        file_server.printFiles();

    }
}
