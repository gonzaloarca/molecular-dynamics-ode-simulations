package ar.edu.itba.ss.odemethods;

import java.util.function.BiFunction;

public class GearPredictorCorrector implements OdeMethod {
    private final BiFunction<Double, Double, Double> force;
    private final double mass;
    private double currentR;
    private double currentR1;
    private double currentR2;
    private double currentR3;
    private double currentR4;
    private double currentR5;

    public GearPredictorCorrector(double r0, double v0, BiFunction<Double, Double, Double> force, double mass) {
        this.force = force;
        this.mass = mass;
        this.currentR5 = 0;
        this.currentR4 = 0;
        this.currentR3 = 0;
        this.currentR2 = force.apply(r0, v0) / mass;
        this.currentR1 = v0;
        this.currentR = r0;
    }

    private double getRPredicted(double stepSize) {
        return currentR
                + currentR1 * stepSize
                + currentR2 * stepSize * stepSize / 2.0
                + currentR3 * stepSize * stepSize * stepSize / 6.0
                + currentR4 * stepSize * stepSize * stepSize * stepSize / 24.0
                + currentR5 * stepSize * stepSize * stepSize * stepSize * stepSize / 120.0;
    }

    private double getR1Predicted(double stepSize) {
        return currentR1
                + currentR2 * stepSize
                + currentR3 * stepSize * stepSize / 2.0
                + currentR4 * stepSize * stepSize * stepSize / 6.0
                + currentR5 * stepSize * stepSize * stepSize * stepSize / 24.0;
    }

    private double getR2Predicted(double stepSize) {
        return currentR2
                + currentR3 * stepSize
                + currentR4 * stepSize * stepSize / 2.0
                + currentR5 * stepSize * stepSize * stepSize / 6.0;
    }

    private double getR3Predicted(double stepSize) {
        return currentR3
                + currentR4 * stepSize
                + currentR5 * stepSize * stepSize / 2.0;
    }

    private double getR4Predicted(double stepSize) {
        return currentR4
                + currentR5 * stepSize;
    }

    private double getR5Predicted(double stepSize) {
        return currentR5;
    }

    private double getRCorrected(double stepSize) {
        double deltaAcc = force.apply(getRPredicted(stepSize), getR1Predicted(stepSize)) / mass - getR2Predicted(stepSize);
        double deltaR2 = deltaAcc * stepSize * stepSize / 2.0;

        double alpha0 = 3.0 / 16.0;
        this.currentR = getRPredicted(stepSize) + alpha0 * deltaR2;

        return this.currentR;
    }

    private double getR1Corrected(double stepSize) {
        double deltaAcc = force.apply(getRPredicted(stepSize), getR1Predicted(stepSize)) / mass - getR2Predicted(stepSize);
        double deltaR2 = deltaAcc * stepSize * stepSize / 2.0;

        double alpha1 = 251.0 / 360.0;
        this.currentR1 = getR1Predicted(stepSize) + alpha1 * deltaR2 * (1.0 / stepSize);

        return this.currentR1;
    }

    private double getR2Corrected(double stepSize) {
        double deltaAcc = force.apply(getRPredicted(stepSize), getR1Predicted(stepSize)) / mass - getR2Predicted(stepSize);
        double deltaR2 = deltaAcc * stepSize * stepSize / 2.0;

        double alpha2 = 1.0;
        this.currentR2 = getR2Predicted(stepSize) + alpha2 * deltaR2 * (2.0 / stepSize * stepSize);

        return this.currentR2;
    }

    private double getR3Corrected(double stepSize) {
        double deltaAcc = force.apply(getRPredicted(stepSize), getR1Predicted(stepSize)) / mass - getR2Predicted(stepSize);
        double deltaR2 = deltaAcc * stepSize * stepSize / 2.0;

        double alpha3 = 11.0 / 18.0;
        this.currentR3 = getR3Predicted(stepSize) + alpha3 * deltaR2 * (6.0 / stepSize * stepSize * stepSize);

        return this.currentR3;
    }

    private double getR4Corrected(double stepSize) {
        double deltaAcc = force.apply(getRPredicted(stepSize), getR1Predicted(stepSize)) / mass - getR2Predicted(stepSize);
        double deltaR2 = deltaAcc * stepSize * stepSize / 2.0;

        double alpha4 = 1.0 / 6.0;
        this.currentR4 = getR4Predicted(stepSize) + alpha4 * deltaR2 * (24.0 / stepSize * stepSize * stepSize * stepSize);

        return this.currentR4;
    }

    private double getR5Corrected(double stepSize) {
        double deltaAcc = force.apply(getRPredicted(stepSize), getR1Predicted(stepSize)) / mass - getR2Predicted(stepSize);
        double deltaR2 = deltaAcc * stepSize * stepSize / 2.0;

        double alpha5 = 1.0 / 60.0;
        this.currentR5 = getR5Predicted(stepSize) + alpha5 * deltaR2 * (120.0 / stepSize * stepSize * stepSize * stepSize * stepSize);

        return this.currentR5;
    }

    private double getNextPosition(double stepSize) {
        getR1Corrected(stepSize);
        getR2Corrected(stepSize);
        getR3Corrected(stepSize);
        getR4Corrected(stepSize);
        getR5Corrected(stepSize);

        return getRCorrected(stepSize);
    }

    @Override
    public double[] solve(int steps, double stepSize) {
        double[] positions = new double[steps];

        positions[0] = this.currentR;

        for(int step = 1; step < steps; step++) {
            positions[step] = getNextPosition(stepSize);
        }

        return positions;
    }

}
