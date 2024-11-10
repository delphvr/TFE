package calendarapp.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;


@Entity
@Table(name = "professions")
public class Profession {

    @Id
    @Column(name = "profession", nullable=false)
    private String profession;

    public Profession() {}

    public Profession(String profession) {
        this.profession = profession;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
    
}
