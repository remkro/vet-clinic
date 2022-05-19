package pl.kurs.vetclinic.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vetclinic.validation.annotation.CorrectNip;
import pl.kurs.vetclinic.validation.annotation.SupportedAnimal;
import pl.kurs.vetclinic.validation.annotation.SupportedMedicalSpecialization;
import pl.kurs.vetclinic.validation.annotation.UniqueNip;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddDoctorCommand {

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String firstName;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String lastName;

    @SupportedMedicalSpecialization(message = "MEDICAL_SPECIALIZATION_NOT_SUPPORTED")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String medicalSpecialization;

    @SupportedAnimal(message = "ANIMAL_NOT_SUPPORTED")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String petSpecialization;

    @Positive(message = "RATE_CANNOT_BE_NEGATIVE")
    private int hourlyRate;

    @NotBlank(message = "NIP_CANNOT_BE_EMPTY")
    @UniqueNip(message = "NIP_CANNOT_BE_DUPLICATED")
    @CorrectNip(message = "INVALID_NIP")
    private String nip;

    @NotNull(message = "FIELD_CANNOT_BE_EMPTY")
    private Boolean currentlyEmployed;

}
