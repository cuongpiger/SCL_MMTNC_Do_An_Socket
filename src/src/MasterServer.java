import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MasterServer {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MasterServerUI().start();
            }
        });
    }
}