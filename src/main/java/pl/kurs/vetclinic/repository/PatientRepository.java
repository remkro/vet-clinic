package pl.kurs.vetclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.vetclinic.model.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
