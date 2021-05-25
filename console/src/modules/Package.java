package modules;

import java.io.Serializable;

public class Package implements Serializable {
    private String iService; // dịch vụ gửi gói tin
    private String iMessage; // nội dung tin nhắn đính kèm theo nếu có
    private Object iContent; // tài nguyên chính cần gửi

    public Package(String pService, String pMessage, Object pContent) {
        iService = pService;
        iMessage = pMessage;
        iContent = pContent;
    }

    public String getiService() {
        return iService;
    }

    public String getiMessage() {
        return iMessage;
    }

    public Object getiContent() {
        return iContent;
    }
}
