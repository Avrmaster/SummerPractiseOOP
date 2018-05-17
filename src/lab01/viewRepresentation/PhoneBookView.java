package lab01.viewRepresentation;

import com.sun.istack.internal.NotNull;
import lab01.businessModel.Person;
import lab01.businessModel.PhoneBook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class PhoneBookView extends JFrame {
    private final PhoneBook phoneBook;
    private JTextField searchInputField;
    private JPanel resultsPanel;

    public PhoneBookView(final @NotNull PhoneBook phoneBook, final Runnable onClose) {
        this.phoneBook = phoneBook;
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Font fontBig = new Font("SansSerif", Font.BOLD, 60);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.PAGE_START;

        searchInputField = new JTextField();
        searchInputField.setFont(fontBig);
        searchInputField.setBackground(Color.LIGHT_GRAY);
        searchInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                onQueryText(searchInputField.getText());
            }
        });
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        this.add(searchInputField, c);

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new GridBagLayout());
        c.weighty = 0.9;
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(new JScrollPane(resultsPanel), c);


        this.setBackground(Color.WHITE);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(screenSize.width * 2 / 3, screenSize.height * 2 / 3);
        this.setLocation(screenSize.width / 6, screenSize.height / 6);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose.run();
            }
        });
        this.onQueryText("");
    }

    private void onQueryText(String text) {
//        resultsPanel.setLayout(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.anchor = GridBagConstraints.PAGE_START;
//        resultsPanel.removeAll();
//        int y = 0;
//        for (Person p : text.length() == 0? phoneBook.getPersons() : phoneBook.find(text)) {
//            c.gridx = 0;
//            c.gridy = y++;
//            c.weighty = c.weightx = 1;
//            resultsPanel.add(new PersonView(p), c);
//        }
//        resultsPanel.updateUI();

        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        for (Person p : text.length() == 0? phoneBook.getPersons() : phoneBook.find(text)) {
            resultsPanel.add(new PersonView(p));
        }
        resultsPanel.updateUI();
    }

    private class PersonView extends JPanel {
        private PersonView(final @NotNull Person person) {
            Font fontSmall = new Font("SansSerif", Font.PLAIN, 40);

            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.PAGE_START;
            c.gridwidth = c.gridheight = 1;
            c.weighty = 1;

            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.6;
            c.gridx = 0;
            c.gridy = 0;
            JTextField textField = new JTextField(person + " - " + phoneBook.getPhones(person));
            textField.setFont(fontSmall);
            textField.setEditable(false);
            this.add(textField, c);

            JButton editButton = new JButton("Edit");
            editButton.setFont(fontSmall.deriveFont(fontSmall.getSize()*0.87f));
            editButton.addActionListener(e -> {

            });
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.4;
            c.gridx = 1;
            c.gridy = 0;
            this.add(editButton, c);

        }
    }

}
