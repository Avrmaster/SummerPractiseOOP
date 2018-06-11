package lab05Plus;

import javax.swing.*;

public class Launcher  {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        new Editor();
    }

}
