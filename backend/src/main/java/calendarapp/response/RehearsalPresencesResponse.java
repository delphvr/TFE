package calendarapp.response;

import java.util.List;

import calendarapp.model.User;

public class RehearsalPresencesResponse {
    private List<User> present;
    private List<User> notPresent;

    public RehearsalPresencesResponse() {
    }

    public RehearsalPresencesResponse(List<User> present, List<User> notPresent) {
        this.present = present;
        this.notPresent = notPresent;
    }

    public List<User> getPresent() {
        return present;
    }

    public List<User> getNotPresent() {
        return notPresent;
    }

    public void setPresent(List<User> present) {
        this.present = present;
    }

    public void setNotPresent(List<User> notPresent) {
        this.notPresent = notPresent;
    }
}
