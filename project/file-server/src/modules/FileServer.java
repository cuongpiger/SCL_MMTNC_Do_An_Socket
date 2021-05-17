package modules;

public class FileServer {
    private static HostInfo iLocal;

    public FileServer() {
        iLocal = Utils.loadHostInfo("./config/local.txt");

        System.out.println(">> FileServer constructor: " + iLocal.getiAddress());
    }

    public HostInfo getiLocal() {
        return iLocal;
    }
}
