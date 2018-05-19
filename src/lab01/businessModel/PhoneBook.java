package lab01.businessModel;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.*;

public class PhoneBook implements Serializable {

    private static final String FILENAME = "phoneBook.secure";

    private Map<Person, Set<String>> phoneBook;
    private static PhoneBook instance;

    public static PhoneBook getInstance() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
            instance = (PhoneBook) ois.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            /* doesn't matter */
        }
        if (instance == null)
            instance = new PhoneBook();
        return instance;
    }

    private PhoneBook() {
        phoneBook = new HashMap<>();
    }

    public boolean addPerson(final @NotNull Person person) {
        for (Person p : find(person.fullName))
            if (p != person && p.fullName.equals(person.fullName))
                return false;
        phoneBook.put(person, new LinkedHashSet<>());
        return true;
    }

    public boolean addPhone(final @NotNull Person person, final @NotNull String newPhone) {
        return phoneBook.get(person).add(newPhone);
    }

    public Set<String> getPhones(final @NotNull Person person) {
        return phoneBook.getOrDefault(person, new HashSet<>());
    }

    public Set<Person> getPersons() {
        return phoneBook.keySet();
    }

    public void removePerson(final @NotNull Person person) {
        phoneBook.remove(person);
    }

    public Set<Person> find(String query) {
        query = query.toLowerCase();
        Set<Person> results = new LinkedHashSet<>();
        for (Map.Entry<Person, Set<String>> entry : phoneBook.entrySet()) {
            boolean match = entry.getKey().fullName.toLowerCase().contains(query);
            if (!match)
                for (String phone : entry.getValue()) {
                    match = phone.toLowerCase().contains(query);
                    if (match)
                        break;
                }
            if (match)
                results.add(entry.getKey());
        }
        return results;
    }

    public void save() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME));
        oos.writeObject(this);
    }

    @Override
    public String toString() {
        return "PhoneBook: " + phoneBook;
    }

}
