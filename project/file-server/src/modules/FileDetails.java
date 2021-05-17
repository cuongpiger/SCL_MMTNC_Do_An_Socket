package modules;

/*
* Dùng để lưu thông tin chi tiết một file bao gồm những gì
* */
public class FileDetails {
    private String iName; // tên file
    private long iSize; // kích thước của file, tính theo bytes

    public FileDetails(String pName, long pSize) {
        iName = pName;
        iSize = pSize;
    }

    public String getiName() {
        return iName;
    }

    public long getiSize() {
        return iSize;
    }
}
