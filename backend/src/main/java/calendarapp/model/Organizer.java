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
    @Column(name = "user_id", nullable=false)
    private Long userId; 

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName="id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public Organizer() {}

    public Organizer(Long userId) {
        this.userId = userId;
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
    }

}
