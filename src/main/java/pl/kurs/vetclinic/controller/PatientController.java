package pl.kurs.vetclinic.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.vetclinic.command.AddPatientCommand;
import pl.kurs.vetclinic.error.exception.NoEntityException;
import pl.kurs.vetclinic.error.exception.WrongIdException;
import pl.kurs.vetclinic.model.dto.PatientDto;
import pl.kurs.vetclinic.model.entity.Patient;
import pl.kurs.vetclinic.model.enums.PetType;
import pl.kurs.vetclinic.service.PatientManagementService;
import pl.kurs.vetclinic.validation.annotation.PatientIdExists;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientManagementService patientManagementService;
    private final ModelMapper mapper;

    @PostConstruct
    public void initialize() throws WrongIdException, NoEntityException {
        patientManagementService.add(new Patient("Stefan", "Grzyb", "krolak.remek@gmail.com", "jacuś", PetType.DOG));
        patientManagementService.add(new Patient("Zenon", "Korzuch", "zenon@korzuch.pl", "pimpuś", PetType.CAT));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getSingle(@PathVariable @PatientIdExists long id) {
        Patient patient = patientManagementService.show(id);
        return ResponseEntity.ok(mapper.map(patient, PatientDto.class));
    }

    @GetMapping("/")
    public ResponseEntity<List<PatientDto>> getAll(@PageableDefault Pageable pageable) {
        Page<Patient> patientsPage = patientManagementService.showAllPageable(pageable);
        List<PatientDto> patientsDto = patientsPage
                .stream()
                .map(patient -> mapper.map(patient, PatientDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(patientsDto);
    }

    @PostMapping
    public ResponseEntity<PatientDto> add(@RequestBody @Valid AddPatientCommand addPatientCommand) {
        Patient patient = new Patient(
                addPatientCommand.getFirstName(),
                addPatientCommand.getLastName(),
                addPatientCommand.getEmail(),
                addPatientCommand.getPetName(),
                PetType.setFromDescription(addPatientCommand.getPetBreed())
        );
        patient = patientManagementService.add(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(patient, PatientDto.class));
    }

}
