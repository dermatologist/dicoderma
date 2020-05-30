package in.co.dermatologist.dicoderma;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GenderEnum {
    FEMALE("F"),
    MALE("M");

    private final String PatientSex;

    GenderEnum(String f) {
        PatientSex = f;
    }

    @JsonValue
    public String getPatientSex() {
        return PatientSex;
    }
}
