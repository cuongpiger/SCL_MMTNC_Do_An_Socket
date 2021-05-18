import modules.Client;
import modules.ClientUI;

import javax.swing.*;

public class ClientApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Client");
        frame.setSize(700, 500);
        frame.setContentPane(new ClientUI().iMainPnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}