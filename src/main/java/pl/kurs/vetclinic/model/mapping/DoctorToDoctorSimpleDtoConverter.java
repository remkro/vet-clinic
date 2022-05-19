package pl.kurs.vetclinic.model.mapping;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.model.dto.DoctorSimpleDto;
import pl.kurs.vetclinic.model.entity.Doctor;

@Service
public class DoctorToDoctorSimpleDtoConverter implements Converter<Doctor, DoctorSimpleDto> {

    @Override
    public DoctorSimpleDto convert(MappingContext<Doctor, DoctorSimpleDto> mappingContext) {
        Doctor doctor = mappingContext.getSource();
        return new DoctorSimpleDto(doctor.getId(), doctor.getFirstName() + " " + doctor.getLastName());
    }

}
