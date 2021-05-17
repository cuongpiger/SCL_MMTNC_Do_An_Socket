import modules.Client;
import modules.ClientUI;

import javax.swing.*;

public class ClientApp {
    public static void main(String[] args) {
        Client handler = new Client();

        JFrame frame = new JFrame("File Server");
        frame.setSize(700, 500);
        frame.setContentPane(new ClientUI(handler).iMainPnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}