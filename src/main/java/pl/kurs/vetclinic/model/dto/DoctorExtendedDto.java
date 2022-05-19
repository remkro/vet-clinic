package pl.kurs.vetclinic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DoctorExtendedDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String medType;

    private String petType;

    private Integer hourlyRate;

    private String nip;

    private Boolean currentlyEmployed;

}
