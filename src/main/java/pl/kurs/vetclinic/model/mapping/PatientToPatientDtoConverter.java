package pl.kurs.vetclinic.model.mapping;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.model.dto.PatientDto;
import pl.kurs.vetclinic.model.entity.Patient;
import pl.kurs.vetclinic.model.enums.PetType;

@Service
public class PatientToPatientDtoConverter implements Converter<Patient, PatientDto> {

    @Override
    public PatientDto convert(MappingContext<Patient, PatientDto> mappingContext) {
        Patient patient = mappingContext.getSource();
        return new PatientDto(patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getEmail(),
                patient.getPetName(), patient.getPetType().getDescription());
    }
    
}
