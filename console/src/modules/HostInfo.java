package modules;

import java.io.Serializable;

/*
* Lưu thông tin Ip-Address và Port
* */
public class HostInfo implements Serializable {
    private String iAddress; // Ip-Address
    private int iPort; // Port

    public HostInfo() {
        iAddress = "";
        iPort = -1;
    }

    public HostInfo(String pAddress, int pPort) {
        iAddress = pAddress;
        iPort = pPort;
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
