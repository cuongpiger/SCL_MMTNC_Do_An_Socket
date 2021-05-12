package modules;

public class HostInfo {
    private String hostname;
    private int port;

    public HostInfo(String h, int p) {
        hostname = h;
        port = p;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }


}
