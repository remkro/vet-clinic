package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.validation.annotation.CorrectNip;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class CorrectNipValidator implements ConstraintValidator<CorrectNip, String> {

    @Override
    public boolean isValid(String nip, ConstraintValidatorContext constraintValidatorContext) {
        int size = nip.length();
        if (size != 10) {
            return false;
        }
        int[] weights = {6, 5, 7, 2, 3, 4, 5, 6, 7};
        int j = 0, sum = 0, control = 0;
        int csum = Integer.parseInt(nip.substring(size - 1));
        for (int i = 0; i < size - 1; i++) {
            char c = nip.charAt(i);
            j = Integer.parseInt(String.valueOf(c));
            sum += j * weights[i];
        }
        control = sum % 11;
        return (control == csum);
    }

}
