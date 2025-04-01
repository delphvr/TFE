package calendarapp.response;

import java.util.List;
import calendarapp.model.Rehearsal;

public class RehearsalPrecedenceResponse {
    private List<Rehearsal> previous;
    private List<Rehearsal> following;
    private List<Rehearsal> notConstraint;
    private List<Rehearsal> constraintByOthers;

    public RehearsalPrecedenceResponse() {
    }

    public RehearsalPrecedenceResponse(List<Rehearsal> previous, List<Rehearsal> following, 
                                       List<Rehearsal> notConstraint, List<Rehearsal> constraintByOthers) {
        this.previous = previous;
        this.following = following;
        this.notConstraint = notConstraint;
        this.constraintByOthers = constraintByOthers;
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

    public List<Rehearsal> getConstraintByOthers() {
        return constraintByOthers;
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

    public void setConstraintByOthers(List<Rehearsal> constraintByOthers) {
        this.constraintByOthers = constraintByOthers;
    }
}
