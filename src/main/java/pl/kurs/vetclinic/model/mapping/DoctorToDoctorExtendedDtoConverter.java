package pl.kurs.vetclinic.model.mapping;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.model.dto.DoctorExtendedDto;
import pl.kurs.vetclinic.model.entity.Doctor;

@Service
public class DoctorToDoctorExtendedDtoConverter implements Converter<Doctor, DoctorExtendedDto> {

    @Override
    public DoctorExtendedDto convert(MappingContext<Doctor, DoctorExtendedDto> mappingContext) {
        Doctor doctor = mappingContext.getSource();
        return new DoctorExtendedDto(doctor.getId(), doctor.getFirstName(), doctor.getLastName(),
                doctor.getMedType().getDescription(), doctor.getPetType().getDescription(),
                doctor.getHourlyRate(), doctor.getNip(), doctor.getCurrentlyEmployed());
    }

}
