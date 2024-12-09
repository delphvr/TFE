package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
