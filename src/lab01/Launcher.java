package lab01;

import lab01.businessModel.PhoneBook;
import lab01.viewRepresentation.PhoneBookView;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args) {
        new Launcher();
    }

    private Launcher() {
        final PhoneBook phoneBook = PhoneBook.getInstance();
        new PhoneBookView(phoneBook, () -> {
            try {
                phoneBook.save();
            } catch (IOException e) {
                System.out.println("Couldn't save phone book because of: "+e);
            }
        });
    }
}
