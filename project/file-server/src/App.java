import modules.FileServer;
import modules.FileServerUI;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        FileServer local = new FileServer();

        JFrame frame = new JFrame("File Server");
        frame.setSize(700, 500);
        frame.setContentPane(new FileServerUI(local).iMainPnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
