package pl.kurs.vetclinic.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vetclinic.validation.annotation.DateAvailable;
import pl.kurs.vetclinic.validation.annotation.DoctorIdExists;
import pl.kurs.vetclinic.validation.annotation.Employed;
import pl.kurs.vetclinic.validation.annotation.NotPast;
import pl.kurs.vetclinic.validation.annotation.NotTooSoon;
import pl.kurs.vetclinic.validation.annotation.PatientIdExists;
import pl.kurs.vetclinic.validation.annotation.WorkingHours;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@GroupSequence({FirstOrder.class, SecondOrder.class, ThirdOrder.class, AddVisitCommand.class})
public class AddVisitCommand {

    @DoctorIdExists(message = "DOCTOR_ID_NOT_FOUND", groups = SecondOrder.class)
    @Employed(message = "DOCTOR_UNEMPLOYED", groups = ThirdOrder.class)
    @NotNull(message = "FIELD_CANNOT_BE_NULL", groups = FirstOrder.class)
    private Long doctorId;

    @PatientIdExists(message = "PATIENT_ID_NOT_FOUND", groups = SecondOrder.class)
    @NotNull(message = "FIELD_CANNOT_BE_NULL", groups = FirstOrder.class)
    private Long patientId;

    @Pattern(regexp = "^([1-9]|([012][0-9])|(3[01]))-([0]{0,1}[1-9]|1[012])-\\d\\d\\d\\d (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$", message = "INVALID_DATE", groups = FirstOrder.class)
    @NotPast(message = "CANNOT_BOOK_VISIT_IN_PAST", groups = SecondOrder.class)
    @NotTooSoon(message = "CANNOT_BOOK_VISIT_ONE_DAY_BEFORE_REQUESTED_DATE", groups = SecondOrder.class)
    @WorkingHours(message = "CANNOT_BOOK_VISIT_OUTSIDE_WORKING_HOURS", groups = SecondOrder.class)
    @DateAvailable(message = "DATE_ALREADY_BOOKED", groups = ThirdOrder.class)
    private String date;

}
