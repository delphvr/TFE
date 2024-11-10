package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
