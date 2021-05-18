package modules;

import java.io.Serializable;
import java.util.ArrayList;

/*
* Một container dùng để chứa thông tin về address + port của file server
* đồng thời thông tin về các file mà file server này đang có
* */
public class FileContainer implements Serializable {
    private HostInfo iFileServer;
    private ArrayList<FileDetails> iFiles;

    public FileContainer(HostInfo pFileServer, ArrayList<FileDetails> pFiles) {
        iFileServer = pFileServer;
        iFiles = pFiles;
    }

    public HostInfo getiFileServer() {
        return iFileServer;
    }

    public ArrayList<FileDetails> getiFiles() {
        return iFiles;
    }
}
