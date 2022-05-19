package pl.kurs.vetclinic.validation.logic;

import org.springframework.stereotype.Service;
import pl.kurs.vetclinic.model.enums.PetType;
import pl.kurs.vetclinic.validation.annotation.SupportedAnimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class SupportedAnimalValidator implements ConstraintValidator<SupportedAnimal, String> {

    @Override
    public boolean isValid(String animal, ConstraintValidatorContext constraintValidatorContext) {
        PetType type = PetType.setFromDescription(animal);
        return type != null;
    }

}
