package pl.kurs.vetclinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vetclinic.model.entity.Patient;
import pl.kurs.vetclinic.repository.PatientRepository;

@Service
public class PatientManagementService extends GenericManagementService<Patient, PatientRepository, Long> {

    public PatientManagementService(PatientRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    public Page<Patient> showAllPageable(Pageable pageable) {
        return repository.findAll(pageable);
    }

}
