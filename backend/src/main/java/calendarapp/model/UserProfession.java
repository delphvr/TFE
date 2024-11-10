package calendarapp.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "users_professions")
@IdClass(UserProfessionId.class)
public class UserProfession {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "profession", nullable = false)
    private String profession;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "profession", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Profession professionEntity;

    public UserProfession() {}

    public UserProfession(Long userId, String profession) {
        this.userId = userId;
        this.profession = profession;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Profession getProfessionEntity() {
        return professionEntity;
    }

    public void setProfessionEntity(Profession professionEntity) {
        this.professionEntity = professionEntity;
    }
}
