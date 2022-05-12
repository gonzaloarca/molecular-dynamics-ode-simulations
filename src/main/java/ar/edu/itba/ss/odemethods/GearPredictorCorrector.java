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

    public GearPredictorCorrector(double r0, double v0, double initialR3, double initialR4, double initialR5, BiFunction<Double, Double, Double> force, double mass) {
        this.force = force;
        this.mass = mass;
        this.currentR5 = initialR5;
        this.currentR4 = initialR4;
        this.currentR3 = initialR3;
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

    private double getRCorrected(double stepSize, double deltaR2, double rPredicted) {
        double alpha0 = 3.0 / 16.0;

        return rPredicted + alpha0 * deltaR2;
    }

    private double getR1Corrected(double stepSize, double deltaR2, double r1Predicted) {
        double alpha1 = 251.0 / 360.0;

        return r1Predicted + alpha1 * deltaR2 * (1.0 / stepSize);
    }

    private double getR2Corrected(double stepSize, double deltaR2, double r2Predicted) {
        double alpha2 = 1.0;

        return r2Predicted + alpha2 * deltaR2 * (2.0 / (stepSize * stepSize));
    }

    private double getR3Corrected(double stepSize, double deltaR2, double r3Predicted) {
        double alpha3 = 11.0 / 18.0;

        return r3Predicted + alpha3 * deltaR2 * (6.0 / (stepSize * stepSize * stepSize));
    }

    private double getR4Corrected(double stepSize, double deltaR2, double r4Predicted) {

        double alpha4 = 1.0 / 6.0;
        return r4Predicted + alpha4 * deltaR2 * (24.0 / (stepSize * stepSize * stepSize * stepSize));
    }

    private double getR5Corrected(double stepSize, double deltaR2, double r5Predicted) {

        double alpha5 = 1.0 / 60.0;

        return r5Predicted + alpha5 * deltaR2 * (120.0 / (stepSize * stepSize * stepSize * stepSize * stepSize));
    }

    @Override
    public double getNextPosition(double stepSize) {
        double r5Predicted = getR5Predicted(stepSize);
        double r4Predicted = getR4Predicted(stepSize);
        double r3Predicted = getR3Predicted(stepSize);
        double r2Predicted = getR2Predicted(stepSize);
        double r1Predicted = getR1Predicted(stepSize);
        double rPredicted = getRPredicted(stepSize);

        double deltaAcc = force.apply(rPredicted, r1Predicted) / mass - r2Predicted;
        double deltaR2 = deltaAcc * stepSize * stepSize / 2.0;

        double r5Corrected = getR5Corrected(stepSize, deltaR2, r5Predicted);
        double r4Corrected = getR4Corrected(stepSize, deltaR2, r4Predicted);
        double r3Corrected = getR3Corrected(stepSize, deltaR2, r3Predicted);
        double r2Corrected = getR2Corrected(stepSize, deltaR2, r2Predicted);
        double r1Corrected = getR1Corrected(stepSize, deltaR2, r1Predicted);
        double rCorrected = getRCorrected(stepSize, deltaR2, rPredicted);

        this.currentR5 = r5Corrected;
        this.currentR4 = r4Corrected;
        this.currentR3 = r3Corrected;
        this.currentR2 = r2Corrected;
        this.currentR1 = r1Corrected;
        this.currentR = rCorrected;

        return rCorrected;
    }

    @Override
    public double getNextVelocity(double stepSize) {
        return currentR1;
    }

    @Override
    public double[] solve(int steps, double stepSize) {
        double[] positions = new double[steps];

        positions[0] = this.currentR;

        for (int step = 1; step < steps; step++) {
            positions[step] = getNextPosition(stepSize);
        }

        return positions;
    }

}
