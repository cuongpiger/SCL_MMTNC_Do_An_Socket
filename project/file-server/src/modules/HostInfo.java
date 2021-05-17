package modules;

/*
* Lưu thông tin Ip-Address và Port
* */
public class HostInfo {
    private String iAddress; // Ip-Address
    private int iPort; // Port

    public HostInfo() {
        iAddress = "";
        iPort = -1;
    }

    public void setiAddress(String pAddress) {
        iAddress = pAddress;
    }

    public void setiPort(int pPort) {
        iPort = pPort;
    }

    public String getiAddress() {
        return iAddress;
    }

    public int getiPort() {
        return iPort;
    }
}
