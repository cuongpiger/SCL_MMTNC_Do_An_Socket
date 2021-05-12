package modules;

import java.io.Serializable;

public class FileDetails implements Serializable {
    private String path; // đường dẫn lưu file
    private String file_name; // tên file
    private long size; // kích thước file
    private long size_unit; // đơn vị file

    FileDetails(String p, String fn, long s) {
        path = p;
        file_name = fn;
        size = s;
        size_unit = calSizeUnit(size);
    }

    private long calSizeUnit(long s) {
        if (s <= 1024) {
            return 1;
        }

        if (s <= Math.pow(1024, 2)) {
            return 1024;
        }

        if (s <= Math.pow(1024, 3)) {
            return (long) Math.pow(1024, 2);
        }

        return (long) Math.pow(1024, 3);
    }

    public String getPath() {
        return path;
    }

    public String getFile_name() {
        return file_name;
    }

    public long getSize() {
        return size;
    }

    public String getSizeFormat() {
        return String.format("%.3f %s", (double) (size / size_unit), getSizeUnit());
    }

    private String getSizeUnit() {
        if (size_unit <= 1024) {
            return "b";
        }

        if (size_unit <= Math.pow(1024, 2)) {
            return "kb";
        }

        if (size_unit <= Math.pow(1024, 3)) {
            return "mb";
        }

        return "gb";
    }
}
