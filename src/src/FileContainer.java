import java.io.Serializable;
import java.util.ArrayList;

public class FileContainer implements Serializable {
    private String ip; // IPv4 của file server để client tả file
    private static int port; // PORT của file server để client tải file
    private ArrayList<FileInfo> files; // danh sách các file từ file server tương ứng

    public FileContainer(String i, int p, ArrayList<FileInfo> f) {
        ip = i;
        port = p;
        files = f;
    }

    public ArrayList<FileInfo> getFiles() {
        return files;
    }

    public String getIp() {
        return ip;
    }
}