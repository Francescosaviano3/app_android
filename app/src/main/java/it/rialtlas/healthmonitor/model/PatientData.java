package it.rialtlas.healthmonitor.model;

public class PatientData {
    public static PatientData NULL = PatientData.of("SELEZIONARE", "", "","", "");
    private final String name;
    private final String firstName;
    private final String lastName;
    private String wrist;
    private final String id;

    private PatientData(String name, String firstName, String lastName, String wrist, String id) {
        this.name = name;
        this.id = id;
        this.wrist = wrist;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return this.firstName + " "+ this.lastName;
    }

    public String getWrist() {
        return wrist != null ? wrist : "";
    }

    public PatientData setWrist(String wrist) {
        if (wrist != null)
            this.wrist = wrist;
        return this;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this==NULL ? "" : getFullName() + " [" +getName() + "]";
    }

    public static PatientData of(String name, String firstName, String lastName,  String wrist, String id) {
        return new PatientData(name, firstName, lastName, wrist, id);
    }
}
