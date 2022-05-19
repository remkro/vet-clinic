package pl.kurs.vetclinic.model.enums;

import java.util.stream.Stream;

public enum MedType {

    CARDIOLOGIST("kardiolog"), LARYNGOLOGIST("laryngolog"), NEUROLOGIST("neurolog"),
    UROLOGIST("urolog"), SHITOLOGIST("kupolog"), SURGEON("chirurg");

    private final String description;

    MedType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static MedType setFromString(String description) {
        return Stream.of(MedType.values()).filter(e -> e.description.equalsIgnoreCase(description)).findFirst().orElse(null);
    }

}

