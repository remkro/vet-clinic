package pl.kurs.vetclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.vetclinic.model.entity.Email;

public interface EmailRepository extends JpaRepository<Email, Long> {

    void deleteByEmailAddress(String emailAddress);

}
