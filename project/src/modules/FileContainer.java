package modules;

import java.io.Serializable;
import java.util.ArrayList;

/*
* Một container dùng để chứa thông tin về address + port của file server
* đồng thời thông tin về các file mà file server này đang có
* */
public class FileContainer implements Serializable {
    private HostInfo iFilerServer;
    private ArrayList<FileDetails> iFiles;

    public FileContainer(HostInfo pFileServer, ArrayList<FileDetails> pFiles) {
        iFilerServer = pFileServer;
        iFiles = pFiles;
    }

    public HostInfo getiFilerServer() {
        return iFilerServer;
    }

    public ArrayList<FileDetails> getiFiles() {
        return iFiles;
    }
}
