import modules.MasterServer;
import modules.MasterServerUI;

import javax.swing.*;

public class MasterServerApp {
    public static void main(String[] args) {
        MasterServer handler = new MasterServer();

        JFrame frame = new JFrame("Master Server");
        frame.setSize(700, 500);
        frame.setContentPane(new MasterServerUI(handler).iMainPnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
