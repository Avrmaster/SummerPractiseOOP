package lab01.businessModel;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;
import java.util.UUID;

public class Person implements Serializable {
    private final String ID = UUID.randomUUID().toString();
    public String fullName;

    public Person(final @NotNull String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Person /" + fullName + "/";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return ID.equals(person.ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
