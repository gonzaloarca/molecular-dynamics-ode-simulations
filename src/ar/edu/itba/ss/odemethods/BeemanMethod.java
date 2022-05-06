package ar.edu.itba.ss.odemethods;

import java.util.function.BiFunction;

public class BeemanMethod {

    private double currentPosition;
    private double currentVelocity;
    private double previousPosition;
    private double previousVelocity;
    private double currentAcceleration;
    private double previousAcceleration;
    private BiFunction<Double, Double, Double> force;
    private double mass;
    private double timeStep;

    public BeemanMethod(double r0, double r1, double v0, double v1, BiFunction<Double, Double, Double> force, double mass, double timeStep) {
        // TODO: r0, v0? (para aceleracion en t - delta t)
        this.currentPosition = r1;
        this.currentVelocity = v1;
        this.previousPosition = r0;
        this.previousVelocity = v0;
        this.force = force;
        this.mass = mass;
        this.timeStep = timeStep;
    }

    public double getNextPosition() {

        // guardar a(t) y a(t-delta)
        this.currentAcceleration = force.apply(currentPosition, currentVelocity) / mass;
        this.previousAcceleration = force.apply(previousPosition, previousVelocity) / mass;

        double nextPosition = currentPosition + currentVelocity
                + (2.0 / 3.0) * currentAcceleration * timeStep * timeStep
                - (1.0 / 6.0) * previousAcceleration * timeStep * timeStep;

        this.previousPosition = this.currentPosition;
        this.currentPosition = nextPosition;

        return nextPosition;
    }

    private double getNextPredictedVelocity() {
        return currentVelocity
                + (3.0 / 2.0) * timeStep * currentAcceleration
                - 0.5 * timeStep * previousAcceleration / mass;
    }

    private double getNextCorrectedVelocity() {
        double nextPredictedVelocity = getNextPredictedVelocity();
        double nextAcceleration = force.apply(currentPosition, nextPredictedVelocity);

        return currentVelocity + (1.0 / 3.0) * nextAcceleration * timeStep
                + (5.0 / 6.0) * currentAcceleration * timeStep
                - (1.0 / 6.0) * previousAcceleration * timeStep;
    }

    public double getNextVelocity() {
        this.previousVelocity = this.currentVelocity;
        this.currentVelocity = getNextCorrectedVelocity();

        return this.currentVelocity;
    }
}
