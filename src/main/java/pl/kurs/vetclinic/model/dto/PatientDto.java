package pl.kurs.vetclinic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatientDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String petName;

    private String petBreed;

}

