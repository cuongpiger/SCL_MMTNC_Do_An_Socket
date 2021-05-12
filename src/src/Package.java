import java.io.Serializable;

public class Package implements Serializable {
    private final String iService;
    private String iMessage;
    private Object iContent;

    public Package(String pS, String pM, Object pC) {
        iService = pS;
        iMessage = pM;
        iContent = pC;
    }

    public String getiService() {
        return iService;
    }

    public String getiMessage() { return iMessage; }

    public Object getiContent() {
        return iContent;
    }
}
