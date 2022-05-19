package pl.kurs.vetclinic.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vetclinic.validation.annotation.SupportedAnimal;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddPatientCommand {

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String firstName;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String lastName;

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String email;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String petName;

    @SupportedAnimal(message = "ANIMAL_NOT_SUPPORTED")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String petBreed;

}
