package pl.kurs.vetclinic.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vetclinic.validation.annotation.SupportedAnimal;
import pl.kurs.vetclinic.validation.annotation.SupportedMedicalSpecialization;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@GroupSequence({FirstOrder.class, SecondOrder.class, ThirdOrder.class, CheckVisitCommand.class})
public class CheckVisitCommand {

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY", groups = FirstOrder.class)
    @SupportedMedicalSpecialization(message = "MEDICAL_SPECIALIZATION_NOT_SUPPORTED", groups = SecondOrder.class)
    String type;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY", groups = FirstOrder.class)
    @SupportedAnimal(message = "ANIMAL_NOT_SUPPORTED", groups = SecondOrder.class)
    String animal;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY", groups = FirstOrder.class)
    @Pattern(regexp = "^(0[1-9]|1\\d|2[0-8]|29(?=-\\d\\d-(?!1[01345789]00|2[1235679]00)\\d\\d(?:[02468][048]|[13579][26]))|30(?!-02)|31(?=-0[13578]|-1[02]))-(0[1-9]|1[0-2])-([12]\\d{3}) ([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "INVALID_DATE", groups = SecondOrder.class)
    String from;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY", groups = FirstOrder.class)
    @Pattern(regexp = "^(0[1-9]|1\\d|2[0-8]|29(?=-\\d\\d-(?!1[01345789]00|2[1235679]00)\\d\\d(?:[02468][048]|[13579][26]))|30(?!-02)|31(?=-0[13578]|-1[02]))-(0[1-9]|1[0-2])-([12]\\d{3}) ([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "INVALID_DATE", groups = SecondOrder.class)
    String to;

}
