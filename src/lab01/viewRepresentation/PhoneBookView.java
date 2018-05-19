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
import java.util.LinkedList;

public class PhoneBookView extends JFrame implements EditableList.ItemChangeListener {
    private final PhoneBook phoneBook;
    private JTextField searchInputField;
    private EditableList editableList;

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

        c.weighty = 0.9;
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(editableList = new EditableList(this), c);

        this.setBackground(Color.WHITE);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(screenSize.width * 2 / 3, screenSize.height * 2 / 3);
        this.setLocation(screenSize.width / 6, screenSize.height / 6);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (onClose != null)
                    onClose.run();
            }
        });

        this.onQueryText("");
    }

    private void onQueryText(String query) {
        java.util.List<String> texts = new LinkedList<>();
        for (Person p : (query.length() == 0 ? phoneBook.getPersons() : phoneBook.find(query)))
            texts.add(p + " - " + phoneBook.getPhones(p));
        editableList.updateResultsPanel(texts);
    }

    @Override
    public void onItemChanged(String newValue) {

    }
}

class EditableList extends JPanel {
    private final JPanel resultsPanel;
    private final Font fontSmall = new Font("SansSerif", Font.PLAIN, 60);
    private final ItemChangeListener changeListener;

    interface ItemChangeListener {
        void onItemChanged(final String newValue);
    }

    EditableList(ItemChangeListener changeListener) {
        this.changeListener = changeListener;

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = c.weighty = c.gridwidth = c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;

        this.add(new JScrollPane(resultsPanel), c);
        updateResultsPanel(null);
    }

    void updateResultsPanel(java.util.List<String> newList) {
        resultsPanel.removeAll();
        if (newList != null)
            for (String s : newList)
                resultsPanel.add(new Item(s, changeListener));
        resultsPanel.updateUI();
    }

    private class Item extends JPanel {
        private String text;

        private Item(final @NotNull String text, final @NotNull ItemChangeListener listener) {
            this.text = text;
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.PAGE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weighty = c.gridwidth = c.gridheight = 1;
            c.gridy = 0;

            c.weightx = 0.8;
            c.gridx = 0;
            JTextField textField = new JTextField(text);
            textField.setFont(fontSmall);
            textField.setEditable(false);
            this.add(textField, c);

            JButton editButton = new JButton("Edit");
            editButton.setFont(fontSmall.deriveFont(fontSmall.getSize() * 0.87f));
            editButton.addActionListener(e -> {
                textField.setEditable(!textField.isEditable());
                editButton.setText(textField.isEditable() ? "Finish" : "Edit");
                if (!textField.isEditable())
                    listener.onItemChanged(textField.getText());
            });

            c.weightx = 0.2;
            c.gridx = 1;
            this.add(editButton, c);
            this.setMinimumSize(new Dimension(Integer.MAX_VALUE, 100));
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;
            return text.equals(item.text);
        }

        @Override
        public int hashCode() {
            return text.hashCode();
        }
    }

}