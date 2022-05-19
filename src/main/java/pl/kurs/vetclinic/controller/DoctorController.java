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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.vetclinic.command.AddDoctorCommand;
import pl.kurs.vetclinic.command.FirstOrder;
import pl.kurs.vetclinic.command.SecondOrder;
import pl.kurs.vetclinic.error.exception.NoEntityException;
import pl.kurs.vetclinic.error.exception.WrongIdException;
import pl.kurs.vetclinic.model.dto.DoctorExtendedDto;
import pl.kurs.vetclinic.model.entity.Doctor;
import pl.kurs.vetclinic.model.enums.MedType;
import pl.kurs.vetclinic.model.enums.PetType;
import pl.kurs.vetclinic.service.DoctorManagementService;
import pl.kurs.vetclinic.validation.annotation.DoctorIdExists;
import pl.kurs.vetclinic.validation.annotation.Employed;

import javax.annotation.PostConstruct;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@GroupSequence({FirstOrder.class, SecondOrder.class, DoctorController.class})
@Validated
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorManagementService doctorManagementService;
    private final ModelMapper mapper;

    @PostConstruct
    public void initialize() throws WrongIdException, NoEntityException {
        doctorManagementService.add(new Doctor("Andrzej", "Mucha", MedType.CARDIOLOGIST, PetType.DOG,
                50, "7615196671", true));
        doctorManagementService.add(new Doctor("Stefan", "Słoń", MedType.SHITOLOGIST, PetType.CAT,
                60, "7583588757", true));
        doctorManagementService.add(new Doctor("Zenon", "Bąk", MedType.SURGEON, PetType.MOSQUITO,
                60, "5338599893", true));
        doctorManagementService.add(new Doctor("Stefan", "Byk", MedType.CARDIOLOGIST, PetType.DOG,
                80, "5224300689", false));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorExtendedDto> getSingle(@PathVariable @DoctorIdExists long id) {
        Doctor doctor = doctorManagementService.show(id);
        return ResponseEntity.ok(mapper.map(doctor, DoctorExtendedDto.class));
    }

    @GetMapping
    public ResponseEntity<List<DoctorExtendedDto>> getAll(@PageableDefault Pageable pageable) {
        Page<Doctor> doctorsPage = doctorManagementService.showAllPageable(pageable);
        List<DoctorExtendedDto> doctorsDto = doctorsPage
                .stream()
                .map(doctor -> mapper.map(doctor, DoctorExtendedDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorsDto);
    }

    @PostMapping
    public ResponseEntity<DoctorExtendedDto> add(@RequestBody @Valid AddDoctorCommand addDoctorCommand) {
        Doctor doctor = new Doctor(
                addDoctorCommand.getFirstName(),
                addDoctorCommand.getLastName(),
                MedType.setFromString(addDoctorCommand.getMedicalSpecialization()),
                PetType.setFromDescription(addDoctorCommand.getPetSpecialization()),
                addDoctorCommand.getHourlyRate(),
                addDoctorCommand.getNip(),
                addDoctorCommand.getCurrentlyEmployed()
        );
        doctor = doctorManagementService.add(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(doctor, DoctorExtendedDto.class)); //ResponseEntity.ok(mapper.map(doctor, DoctorExtendedDto.class));
    }

    @PutMapping("/{id}/fire")
    public ResponseEntity<DoctorExtendedDto> fire(@PathVariable
                                                  @DoctorIdExists(groups = FirstOrder.class)
                                                  @Employed(groups = SecondOrder.class) long id) {
        Doctor doctor = doctorManagementService.fire(id);
        return ResponseEntity.ok(mapper.map(doctor, DoctorExtendedDto.class));
    }

}
