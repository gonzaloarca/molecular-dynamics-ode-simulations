package ar.edu.itba.ss.odemethods;

import java.util.function.BiFunction;

public class BeemanMethod implements OdeMethod {

    private double currentPosition;
    private double currentVelocity;
    private double previousPosition;
    private double previousVelocity;
    private double currentAcceleration;
    private double previousAcceleration;
    private BiFunction<Double, Double, Double> force;
    private double mass;

    public BeemanMethod(double r0, double v0, BiFunction<Double, Double, Double> force, double mass) {
        this.currentPosition = r0;
        this.currentVelocity = v0;

        this.force = force;
        this.mass = mass;
    }

    private double getNextPosition(double stepSize) {
        // guardar a(t) y a(t-delta)
        this.currentAcceleration = force.apply(currentPosition, currentVelocity) / mass;
        this.previousAcceleration = force.apply(previousPosition, previousVelocity) / mass;

        double nextPosition = currentPosition + currentVelocity * stepSize
                + (2.0 / 3.0) * currentAcceleration * stepSize * stepSize
                - (1.0 / 6.0) * previousAcceleration * stepSize * stepSize;

        this.previousPosition = this.currentPosition;
        this.currentPosition = nextPosition;

        return nextPosition;
    }

    private double getNextPredictedVelocity(double stepSize) {
        return currentVelocity
                + (3.0 / 2.0) * stepSize * currentAcceleration
                - 0.5 * stepSize * previousAcceleration;
    }

    private double getNextCorrectedVelocity(double stepSize) {
        double nextPredictedVelocity = getNextPredictedVelocity(stepSize);
        double nextAcceleration = force.apply(currentPosition, nextPredictedVelocity) / mass;

        return currentVelocity + (1.0 / 3.0) * nextAcceleration * stepSize
                + (5.0 / 6.0) * currentAcceleration * stepSize
                - (1.0 / 6.0) * previousAcceleration * stepSize;
    }

    private double getNextVelocity(double stepSize) {
        this.previousVelocity = this.currentVelocity;
        this.currentVelocity = getNextCorrectedVelocity(stepSize);

        return this.currentVelocity;
    }

    @Override
    public double[] solve(int steps, double stepSize) {
        // use euler to calculate previous position and velocity
        EulerMethod euler = new EulerMethod(currentPosition, currentVelocity, force, mass);
        this.previousPosition = euler.getNextPosition(-stepSize);
        this.previousVelocity = euler.getNextVelocity(-stepSize);

        euler.update(this.previousPosition, this.previousVelocity);

        double[] positions = new double[steps];

        positions[0] = this.currentPosition;

        for (int i = 1; i < steps; i++) {
            positions[i] = getNextPosition(stepSize);
            getNextVelocity(stepSize);
        }

        return positions;
    }
}
