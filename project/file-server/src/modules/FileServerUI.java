package modules;

import javax.swing.*;

public class FileServerUI {
    public JPanel iMainPnl;
    private JTable iFilesTbl;
    private JTable iActivitiesTbl;
    private JButton iCloseBtn;
    private JLabel iStatusLbl;

    public FileServerUI(FileServer pLocal) {
        System.out.println(">> FileServerUtil " + pLocal.getiLocal().getiPort());
    }
}
