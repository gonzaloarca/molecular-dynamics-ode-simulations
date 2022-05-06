package ar.edu.itba.ss.odemethods;

import java.util.function.BiFunction;

public class GearPredictorCorrector {
    private final BiFunction<Double, Double, Double> force;
    private final double mass;
    private final double timeStep;
    private final double timeStepPow2;
    private final double timeStepPow3;
    private final double timeStepPow4;
    private final double timeStepPow5;
    private double currentR;
    private double currentR1;
    private double currentR2;
    private double currentR3;
    private double currentR4;
    private double currentR5;

    public GearPredictorCorrector(double r0, double v0, double mass, BiFunction<Double, Double, Double> force, double timeStep) {
        this.timeStep = timeStep;
        this.timeStepPow2 = timeStep * timeStep;
        this.timeStepPow3 = this.timeStepPow2 * timeStep;
        this.timeStepPow4 = this.timeStepPow3 * timeStep;
        this.timeStepPow5 = this.timeStepPow4 * timeStep;
        this.force = force;
        this.mass = mass;
        this.currentR5 = 0;
        this.currentR4 = 0;
        this.currentR3 = 0;
        this.currentR2 = force.apply(r0, v0) / mass;
    }

    private double getRPredicted() {
        return currentR
                + currentR1 * timeStep
                + currentR2 * timeStepPow2 / 2.0
                + currentR3 * timeStepPow3 / 6.0
                + currentR4 * timeStepPow4 / 24.0
                + currentR5 * timeStepPow5 / 120.0;
    }

    private double getR1Predicted() {
        return currentR1
                + currentR2 * timeStep
                + currentR3 * timeStepPow2 / 2.0
                + currentR4 * timeStepPow3 / 6.0
                + currentR5 * timeStepPow4 / 24.0;
    }

    private double getR2Predicted() {
        return currentR2
                + currentR3 * timeStep
                + currentR4 * timeStepPow2 / 2.0
                + currentR5 * timeStepPow3 / 6.0;
    }

    private double getR3Predicted() {
        return currentR3
                + currentR4 * timeStep
                + currentR5 * timeStepPow2 / 2.0;
    }

    private double getR4Predicted() {
        return currentR4
                + currentR5 * timeStep;
    }

    private double getR5Predicted() {
        return currentR5;
    }

    private double getRCorrected() {
        double deltaAcc = force.apply(getRPredicted(), getR1Predicted()) / mass - getR2Predicted();
        double deltaR2 = deltaAcc * timeStepPow2 / 2.0;

        double alpha0 = 3.0 / 16.0;
        this.currentR = getRPredicted() + alpha0 * deltaR2;

        return this.currentR;
    }

    private double getR1Corrected() {
        double deltaAcc = force.apply(getRPredicted(), getR1Predicted()) / mass - getR2Predicted();
        double deltaR2 = deltaAcc * timeStepPow2 / 2.0;

        double alpha1 = 251.0 / 360.0;
        this.currentR1 = getR1Predicted() + alpha1 * deltaR2 * (1.0 / timeStep);

        return this.currentR1;
    }

    private double getR2Corrected() {
        double deltaAcc = force.apply(getRPredicted(), getR1Predicted()) / mass - getR2Predicted();
        double deltaR2 = deltaAcc * timeStepPow2 / 2.0;

        double alpha2 = 1.0;
        this.currentR2 = getR2Predicted() + alpha2 * deltaR2 * (2.0 / timeStepPow2);

        return this.currentR2;
    }

    private double getR3Corrected() {
        double deltaAcc = force.apply(getRPredicted(), getR1Predicted()) / mass - getR2Predicted();
        double deltaR2 = deltaAcc * timeStepPow2 / 2.0;

        double alpha3 = 11.0 / 18.0;
        this.currentR3 = getR3Predicted() + alpha3 * deltaR2 * (6.0 / timeStepPow3);

        return this.currentR3;
    }

    private double getR4Corrected() {
        double deltaAcc = force.apply(getRPredicted(), getR1Predicted()) / mass - getR2Predicted();
        double deltaR2 = deltaAcc * timeStepPow2 / 2.0;

        double alpha4 = 1.0 / 6.0;
        this.currentR4 = getR4Predicted() + alpha4 * deltaR2 * (24.0 / timeStepPow4);

        return this.currentR4;
    }

    private double getR5Corrected() {
        double deltaAcc = force.apply(getRPredicted(), getR1Predicted()) / mass - getR2Predicted();
        double deltaR2 = deltaAcc * timeStepPow2 / 2.0;

        double alpha5 = 1.0 / 60.0;
        this.currentR5 = getR5Predicted() + alpha5 * deltaR2 * (120.0 / timeStepPow5);

        return this.currentR5;
    }

    public double getNextPosition() {
        getR1Corrected();
        getR2Corrected();
        getR3Corrected();
        getR4Corrected();
        getR5Corrected();

        return getRCorrected();
    }

}
