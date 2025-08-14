package models;

import java.util.Objects;

public class Formateur {
    int id;

    String firstName,lastName,email;

    public Formateur(){}

    public Formateur(int id,  String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Formateur formateur)) return false;
        return id == formateur.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
