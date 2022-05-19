package pl.kurs.vetclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.kurs.vetclinic.model.entity.Doctor;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long>, PagingAndSortingRepository<Doctor, Long> {

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Doctor> findWithLockingById(Long id);

    boolean existsByNip(String nip);

}
