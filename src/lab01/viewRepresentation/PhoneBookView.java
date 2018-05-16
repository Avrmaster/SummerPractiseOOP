package lab01.viewRepresentation;

import com.sun.istack.internal.NotNull;
import lab01.businessModel.PhoneBook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PhoneBookView extends JFrame {
    private final PhoneBook phoneBook;

    public PhoneBookView(final @NotNull PhoneBook phoneBook, final Runnable onClose) {
        this.phoneBook = phoneBook;
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        /* Do stuff here */

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(screenSize.width*2/3, screenSize.height*2/3);
        this.setLocation(screenSize.width/6, screenSize.height/6);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose.run();
            }
        });
    }

}
