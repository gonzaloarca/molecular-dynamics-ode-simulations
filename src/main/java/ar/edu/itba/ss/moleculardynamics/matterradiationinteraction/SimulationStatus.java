package ar.edu.itba.ss.moleculardynamics.matterradiationinteraction;

public enum SimulationStatus {
    ABSORBED, NOT_FINISHED, ESCAPED;

    @Override
    public String toString() {
        return switch (this) {
            case ABSORBED -> "A";
            case NOT_FINISHED -> "N";
            case ESCAPED -> "E";
        };
    }
}
