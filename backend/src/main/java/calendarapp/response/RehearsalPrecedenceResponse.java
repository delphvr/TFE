package calendarapp.response;

import java.util.List;

import calendarapp.model.Rehearsal;

public class RehearsalPrecedenceResponse {
    private List<Rehearsal> previous;
    private List<Rehearsal> following;
    private List<Rehearsal> notConstraint;

    public RehearsalPrecedenceResponse() {
    }

    public RehearsalPrecedenceResponse(List<Rehearsal> previous, List<Rehearsal> following, List<Rehearsal> notConstraint) {
        this.previous = previous;
        this.following = following;
        this.notConstraint = notConstraint;
    }

    public List<Rehearsal> getPrevious() {
        return previous;
    }

    public List<Rehearsal> getFollowing() {
        return following;
    }

    public List<Rehearsal> getNotConstraint() {
        return notConstraint;
    }

    public void setPrevious(List<Rehearsal> previous) {
        this.previous = previous;
    }

    public void setFollowing(List<Rehearsal> following) {
        this.following = following;
    }

    public void setNotConstraint(List<Rehearsal> notConstraint) {
        this.notConstraint = notConstraint;
    }
}
