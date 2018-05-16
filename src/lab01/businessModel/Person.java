package lab01.businessModel;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;

public class Person implements Serializable {
    public final String fullName;

    public Person(final @NotNull String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Person {" + fullName + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return fullName.equals(person.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }
}
