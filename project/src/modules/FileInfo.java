package modules;

import java.io.Serializable;

public class FileInfo implements Serializable {
    private HostInfo iHost;
    private FileDetails iFile;

    public FileInfo(HostInfo pHost, FileDetails pFile) {
        iHost = pHost;
        iFile = pFile;
    }

    public HostInfo getiHost() {
        return iHost;
    }

    public FileDetails getiFile() {
        return iFile;
    }

    public boolean match(FileDetails pFile, String pHost) {
        if (pFile.getiName().equals(iFile.getiName()) && pHost.equals(iHost.getiAddress())) {
            return true;
        }

        return false;
    }
}
