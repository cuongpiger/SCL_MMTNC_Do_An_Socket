import java.io.Serializable;
import java.util.ArrayList;

public class FileContainer implements Serializable {
    private String ip; // IPv4 của file server để client tả file
    private ArrayList<FileDetails> files; // danh sách các file từ file server tương ứng
    private static int port; // PORT của file server để client tải file

    public FileContainer(String i, int p, ArrayList<FileDetails> f) {
        ip = i;
        port = p;
        files = f;
    }

    public ArrayList<FileDetails> getFiles() {
        return files;
    }

    public String getIp() {
        return ip;
    }
}