package pl.kurs.vetclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.vetclinic.model.entity.Visit;
import pl.kurs.vetclinic.model.enums.MedType;
import pl.kurs.vetclinic.model.enums.PetType;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitRepository extends JpaRepository<Visit, UUID> {

    List<Visit> getAllByDateIsAfterAndDateIsBeforeAndDoctor_PetTypeAndDoctor_MedType(LocalDateTime start,
                                                                                     LocalDateTime end,
                                                                                     PetType petType, MedType medType);

    Optional<Visit> getByPatientEmail(String email);

}
