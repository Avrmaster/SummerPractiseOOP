package lab01.viewRepresentation;

import com.sun.istack.internal.NotNull;
import lab01.businessModel.Person;
import lab01.businessModel.PhoneBook;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Function;

public class PhoneBookView extends JFrame implements EditableList.ItemChangeListener<Person> {
    private final PhoneBook phoneBook;
    private JTextField searchInputField;
    private EditableList<Person> editableList;

    public PhoneBookView(final @NotNull PhoneBook phoneBook, final Runnable onClose) {
        this.phoneBook = phoneBook;
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Font fontBig = new Font("SansSerif", Font.BOLD, 60);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = c.weighty = c.gridwidth = c.gridheight = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;

        searchInputField = new JTextField();
        searchInputField.setFont(fontBig);
        searchInputField.setBackground(Color.LIGHT_GRAY);
        searchInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            void onChange() {
                onQueryText(searchInputField.getText());
            }
        });
        c.weighty = 0.1;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        this.add(searchInputField, c);

        c.weighty = 0.8;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(editableList = new EditableList<>(this), c);

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

        this.onQueryText();
    }

    private void onQueryText() {
        onQueryText(searchInputField.getText());
    }

    private void onQueryText(String query) {
        editableList.updateResultsPanel(query.length() == 0 ? phoneBook.getPersons() : phoneBook.find(query),
                (p) -> p.fullName, p -> phoneBook.getPhones(p).toString());
    }

    @Override
    public void onItemChanged(Person person, String editedValue) {
        for (Person p : phoneBook.find(editedValue)) {
            if (p != person && p.fullName.equals(editedValue)) {
                JOptionPane.showMessageDialog(this, "Duplicate name!");
                onQueryText();
                return;
            }
        }
        person.fullName = editedValue;
        onQueryText();
    }

    @Override
    public void onItemAdded(String newValue) {
        if (!phoneBook.addPerson(new Person(newValue))) {
            JOptionPane.showMessageDialog(this, "Duplicate name!");
        }
        onQueryText();
    }

    @Override
    public void onItemDeleted(Person person) {
        phoneBook.removePerson(person);
        onQueryText();
    }

    @Override
    public void onItemPressed(Person person) {
        new EditPersonFrame(phoneBook, person, this::onQueryText);
    }
}

class EditableList<ItemData> extends JPanel {
    private final JPanel resultsPanel;
    private final Font fontSmall = new Font("SansSerif", Font.PLAIN, 60);
    private final ItemChangeListener<ItemData> changeListener;

    interface ItemChangeListener<ItemData> {
        void onItemChanged(ItemData itemData, final String editedValue);
        void onItemAdded(final String newValue);
        void onItemDeleted(final ItemData itemData);
        void onItemPressed(final ItemData itemData);
    }

    EditableList(ItemChangeListener<ItemData> changeListener) {
        this.changeListener = changeListener;

        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = c.gridwidth = c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;

        c.weighty = 0.9;
        c.gridx = 0;
        c.gridy = 0;
        this.add(new JScrollPane(resultsPanel), c);

        final JTextField newItemInputField = new JTextField();
        newItemInputField.setFont(fontSmall);

        c.weightx = 0.9;
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = 1;
        this.add(newItemInputField, c);

        JButton addButton = new JButton("+");
        addButton.setFont(fontSmall.deriveFont(60f));
        addButton.addActionListener(e -> {
            String text = newItemInputField.getText();
            if (text.length() < 2)
                JOptionPane.showMessageDialog(this, "Too short name");
            else
                changeListener.onItemAdded(text);
        });

        c.weightx = 0.1;
        c.weighty = 0.1;
        c.gridx = 1;
        c.gridy = 1;
        this.add(addButton, c);

        updateResultsPanel(null, null, null);
    }

    void updateResultsPanel(java.util.Set<ItemData> newList,
                            Function<ItemData, String> editFieldExtractor,
                            Function<ItemData, String> descriptionFieldExtractor) {
        resultsPanel.removeAll();
        if (newList != null)
            for (ItemData itemData : newList)
                resultsPanel.add(new Item(
                        itemData,
                        editFieldExtractor.apply(itemData),
                        descriptionFieldExtractor.apply(itemData),
                        changeListener));
        resultsPanel.updateUI();
    }

    private class Item extends JPanel {
        private final int ITEM_HEIGHT = 100;
        private String text;

        private Item(final @NotNull ItemData itemData, final @NotNull String text, final String description,
                     final @NotNull ItemChangeListener<ItemData> listener) {
            this.text = text;

            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.PAGE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weighty = c.gridwidth = c.gridheight = 1;
            c.gridy = 0;

            c.weightx = 0.5;
            c.gridx = 0;
            JTextField textField = new JTextField(text);
            textField.setFont(fontSmall);
            textField.setEditable(false);
            this.add(textField, c);

            if (description != null) {
                c.weighty = 0.3;
                c.gridx = 1;
                JTextField descriptionField = new JTextField(description);
                descriptionField.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        changeListener.onItemPressed(itemData);
                    }
                });
                descriptionField.setFont(fontSmall);
                descriptionField.setEditable(false);
                this.add(descriptionField, c);
            }

            JButton editButton = new JButton("Edit");
            editButton.setFont(fontSmall.deriveFont(fontSmall.getSize() * 0.87f));
            editButton.addActionListener(e -> {
                textField.setEditable(!textField.isEditable());
                editButton.setText(textField.isEditable() ? "Finish" : "Edit");
                if (!textField.isEditable())
                    listener.onItemChanged(itemData, textField.getText());
            });

            c.weightx = 0.2;
            c.gridx++;
            this.add(editButton, c);

            JButton deleteButton = new JButton("Delete");
            deleteButton.setFont(fontSmall.deriveFont(fontSmall.getSize() * 0.87f));
            deleteButton.addActionListener(e -> listener.onItemDeleted(itemData));
            c.weightx = 0.2;
            c.gridx = 3;
            this.add(deleteButton, c);

            this.setMinimumSize(new Dimension(Integer.MAX_VALUE, ITEM_HEIGHT));
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_HEIGHT));
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

class EditPersonFrame extends JFrame {

    private EditableList<String> editableList;

    EditPersonFrame(final @NotNull PhoneBook phoneBook, final @NotNull Person person, final @NotNull Runnable onUpdated) {
        onUpdated.run();

        this.setBackground(Color.CYAN);
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = c.gridwidth = c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;

        c.gridx = c.gridy = 0;
        c.weighty = 0.1;
        JTextField jTextField = new JTextField(person.toString());
        jTextField.setFont(jTextField.getFont().deriveFont(60f));
        jTextField.setEditable(false);
        this.add(jTextField, c);

        final Runnable onUpdate = () -> {
            editableList.updateResultsPanel(
                    phoneBook.getPhones(person),
                    s->s,
                    s->null);
            onUpdated.run();
        };
        editableList = new EditableList<>(new EditableList.ItemChangeListener<String>() {
            @Override
            public void onItemChanged(String s, String editedValue) {
                phoneBook.getPhones(person).remove(s);
                phoneBook.getPhones(person).add(editedValue);
                onUpdate.run();
            }

            @Override
            public void onItemAdded(String newValue) {
                phoneBook.getPhones(person).add(newValue);
                onUpdate.run();
            }

            @Override
            public void onItemDeleted(String s) {
                phoneBook.getPhones(person).remove(s);
                onUpdate.run();
            }

            @Override
            public void onItemPressed(String s) {
            }

        });

        c.gridy = 1;
        c.weighty = 0.9;
        this.add(editableList, c);
        onUpdate.run();

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBackground(Color.WHITE);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(screenSize.width * 2 / 3, screenSize.height * 2 / 3);
        this.setLocation(screenSize.width / 6, screenSize.height / 6);
        this.setVisible(true);
    }

}