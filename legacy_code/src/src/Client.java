import java.awt.*;

public class Client {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientUI().start();
            }
        });
    }
}