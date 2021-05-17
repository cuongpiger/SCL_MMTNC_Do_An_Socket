package modules;

/*
* Dùng để lưu thông tin chi tiết một file bao gồm những gì
* */
public class FileDetails {
    private String iPath; // đường dẫn lưu file
    private String iName; // tên file
    private long iSize; // kích thước của file, tính theo bytes

    public FileDetails(String pPath, String pName, long pSize) {
        iPath = pPath;
        iName = pName;
        iSize = pSize;
    }
}
