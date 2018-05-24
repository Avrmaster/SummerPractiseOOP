package utils;

import com.sun.istack.internal.NotNull;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

public class JFrameUtils {

    public static void centerAndNormalizeFrame(final JFrame frame) {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocation(screenSize.width/6, screenSize.height/6);
        frame.setSize(screenSize.width*2/3, screenSize.height*2/3);
        frame.setVisible(true);
    }

    public static void promptToChooseFile(final Consumer<String> onFileChosen, String ... extensions) {
        new Thread(() -> {
            final JFileChooser fc = new JFileChooser();
            try {
                fc.setCurrentDirectory(new File(System.getProperty("user.home") + File.separator + "desktop"));
            } catch (Exception e) {/* is directory wasn't found */}
            fc.setFileFilter(new FileNameExtensionFilter("Text", extensions));

            JFrame jFrame = new JFrame();
            jFrame.setAlwaysOnTop(true);

            int returnVal = fc.showOpenDialog(jFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                onFileChosen.accept(fc.getSelectedFile().getPath());
            }
        }).start();
    }

    public static JFrame showTextInFrame(final @NotNull String title, final @NotNull String content) {
        JFrame frame = new JFrame(title);
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        JTextArea jta = new JTextArea(content);
        jta.setEditable(false);

        panel.add(jta);
        frame.add(new JScrollPane(panel));

        // Modify some properties.
        jta.setRows(10);
        jta.setColumns(10);
        panel.setBorder(new EtchedBorder());

        // Display the Swing application.
        centerAndNormalizeFrame(frame);
        return frame;
    }

}
