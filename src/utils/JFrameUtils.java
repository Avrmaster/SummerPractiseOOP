package utils;

import javax.swing.*;
import java.awt.*;

public class JFrameUtils {

    public static void centerAndNormalize(final JFrame frame) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocation(screenSize.width/6, screenSize.height/6);
        frame.setSize(screenSize.width*2/3, screenSize.height*2/3);
        frame.setVisible(true);
    }

}
