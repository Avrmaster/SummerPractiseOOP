package lab01;

import lab01.businessModel.Person;
import lab01.businessModel.PhoneBook;
import lab01.viewRepresentation.PhoneBookView;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args) {
        new Launcher();
    }

    private Launcher() {
        final PhoneBook phoneBook = PhoneBook.getInstance();
        phoneBook.addPhone(new Person("Oleksandr"), "0975315564");
        phoneBook.addPhone(new Person("Andriy"), "0675078361");
        phoneBook.addPhone(new Person("Andriy2"), "0675078361");
        phoneBook.addPhone(new Person("Andriy3"), "0675078361");
        phoneBook.addPhone(new Person("Andriy5"), "0675078361");

        new PhoneBookView(phoneBook, () -> {
            try {
                phoneBook.save();
            } catch (IOException e) {
                System.out.println("Couldn't save phone book because of: "+e);
            }
        });
    }
}
