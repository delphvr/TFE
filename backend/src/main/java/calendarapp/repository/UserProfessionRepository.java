package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import calendarapp.model.UserProfession;
import calendarapp.model.UserProfessionId;

public interface UserProfessionRepository extends JpaRepository<UserProfession, UserProfessionId> {

}
