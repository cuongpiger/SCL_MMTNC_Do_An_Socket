package modules;

public class Activity {
    private String iDescription;
    private HostInfo iHost;

    public Activity(String pDescription, HostInfo pHost) {
        iDescription = pDescription;
        iHost = pHost;
    }

    public String getiDescription() {
        return iDescription;
    }

    public String getAddress() {
        return String.format("%s:%d", iHost.getiAddress(), iHost.getiPort());
    }
}
