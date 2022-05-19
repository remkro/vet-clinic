package pl.kurs.vetclinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vetclinic.error.exception.NoEntityException;
import pl.kurs.vetclinic.error.exception.WrongIdException;
import pl.kurs.vetclinic.model.entity.Doctor;
import pl.kurs.vetclinic.repository.DoctorRepository;

import java.util.Optional;

@Service
public class DoctorManagementService extends GenericManagementService<Doctor, DoctorRepository, Long> {

    public DoctorManagementService(DoctorRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    public Page<Doctor> showAllPageable(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public Doctor showWithLocking(Long id) {
        return repository
                .findWithLockingById(Optional.ofNullable(id).orElseThrow(() -> new WrongIdException("ID_CANNOT_BE_NULL")))
                .orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND"));
    }

    @Transactional
    public Doctor fire(Long id) {
        Doctor doctor = show(id);
        doctor.setCurrentlyEmployed(false);
        doctor = edit(doctor);
        return doctor;
    }

}
