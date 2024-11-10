package calendarapp.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "organizers")
public class Organizer {

    @Id
    @Column(name = "id_user", nullable = false) //TODO: normalement user_id mais alors ça crash...
    //2024-11-10T15:41:56.501+01:00 ERROR 21680 --- [nio-8080-exec-1] org.hibernate.AssertionFailure           : HHH000099: an assertion failure occurred (this may indicate a bug in Hibernate, but is more likely due to unsafe use of the session): org.hibernate.AssertionFailure: null identifier (calendarapp.model.Organizer) 
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public Organizer() {}

    public Organizer(User user) {
        this.user = user;
        this.userId = user.getId(); 
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId(); 
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "userId=" + userId +
                ", user=" + user.toString() + // Assuming User has a `username` field.
                '}';
    }
}
