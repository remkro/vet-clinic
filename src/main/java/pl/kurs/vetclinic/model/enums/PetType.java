package pl.kurs.vetclinic.model.enums;

import java.util.stream.Stream;

public enum PetType {

    DOG("pies"), CAT("kot"), HAMSTER("chomik"), RABBIT("królik"),
    FISH("ryba"), MOUSE("mysz"), SNAKE("wąż"), MOSQUITO("komar");

    private final String description;

    PetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static PetType setFromDescription(String description) {
        return Stream.of(PetType.values()).filter(e -> e.description.equalsIgnoreCase(description)).findFirst().orElse(null);
    }

}
