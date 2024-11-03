package org.example;
import java.util.ArrayList;

public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private boolean isOrganizer;
    private ArrayList<String> professions;

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

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    public ArrayList<String> getProfessions() {
        return professions;
    }

    public void setProfessions(ArrayList<String> professions) {
        this.professions = professions;
    }
}
