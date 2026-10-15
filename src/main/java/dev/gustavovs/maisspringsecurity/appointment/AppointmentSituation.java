package dev.gustavovs.maisspringsecurity.appointment;

public enum AppointmentSituation {

    ACTIVE("active"),
    RESCHEDULED("rescheduled"),
    COMPLETED("completed");

    private final String situation;

    AppointmentSituation(String situation) {
        this.situation = situation;
    }

    public String getSituation() {
        return this.situation;
    }
}
