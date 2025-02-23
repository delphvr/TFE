package calendarapp.request;

import java.util.List;

public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private List<String> professions;
    private boolean isOrganizer; 

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

    public List<String> getProfessions() {
        return professions;
    }

    public void setProfessions(List<String> professions) {
        this.professions = professions;
    }

    public boolean getIsOrganizer() {
        return isOrganizer;
    }

    public void setIsOrganizer(boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    @Override
    public String toString() {
        return "CreateUserRequest{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", professions=" + professions +
               ", isOrganizer=" + isOrganizer +
               '}';
    }
}
