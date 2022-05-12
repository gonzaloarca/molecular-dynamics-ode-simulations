package ar.edu.itba.ss.moleculardynamics.matterradiationinteraction;

public enum SimulationStatus {
    ABSORBED, NOT_FINISHED, BOTTOM_ESCAPED, TOP_ESCAPED, LEFT_ESCAPED, RIGHT_ESCAPED;

    @Override
    public String toString() {
        return switch (this) {
            case ABSORBED -> "A";
            case NOT_FINISHED -> "N";
            case BOTTOM_ESCAPED -> "EB";
            case TOP_ESCAPED -> "ET";
            case LEFT_ESCAPED -> "EL";
            case RIGHT_ESCAPED -> "ER";
        };
    }
}
