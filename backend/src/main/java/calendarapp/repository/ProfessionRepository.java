package calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import calendarapp.model.Profession;

public interface ProfessionRepository extends JpaRepository<Profession, String> {

}
