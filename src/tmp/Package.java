import java.io.Serializable;

public class Package implements Serializable {
    private final String service;
    private Object content;

    public Package(String s, Object c) {
        service = s;
        content = c;
    }

    public String getService() {
        return service;
    }

    public Object getContent() {
        return content;
    }
}
