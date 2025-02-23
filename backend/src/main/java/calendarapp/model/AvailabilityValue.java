package calendarapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "availabilities_values")
public class AvailabilityValue {

    @Id
    @Column(name = "availability", nullable=false)
    private int availability;

    public AvailabilityValue() {}

    public AvailabilityValue(int availability) {
        this.availability = availability;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }
    
}
