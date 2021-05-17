import modules.FileServer;
import modules.FileServerUI;

import javax.swing.*;

public class FileServerApp {
    public static void main(String[] args) {
        FileServer handler = new FileServer();

        JFrame frame = new JFrame("File Server");
        frame.setSize(700, 500);
        frame.setContentPane(new FileServerUI(handler).iMainPnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
