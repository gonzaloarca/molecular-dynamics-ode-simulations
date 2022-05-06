package ar.edu.itba.ss.odemethods;

import java.util.function.BiFunction;

public class VerletMethod {
    private final double initialPosition;
    private final double initialVelocity;
    private final BiFunction<Double, Double, Double> force;
    private final double timeStep;
    private final double mass;
    private double previousPosition;
    private double currentPosition;
    private double currentVelocity;
    private double previousPreviousPosition;
    private double currentTimeStep;

    public VerletMethod(double r0, double r1, double v0, BiFunction<Double, Double, Double> force, double mass, double timeStep) {
        // TODO: r0? para r(t - delta t)
        this.initialPosition = r1;
        this.initialVelocity = v0;
        this.force = force;
        this.timeStep = timeStep;
        this.mass = mass;
        this.currentTimeStep = 0;
        this.currentPosition = r1;
        this.currentVelocity = v0;
        this.previousPosition = r0;
    }

    // r0 = 1
    // nextPosition() -> r1 = r(0 + dt) = 2 * r0 - r(-1) + dt^2 * force(r0,v0)
    // nextPosition() -> r2 = f(r0, v0) --> r(t + dt)
    // nextVelocity() -> v1 = (r2 - r0)/2*delta --> v(t)
    // nextPosition() -> f(r1, v1)
    // nextVelocity() -> v2 = (r1 - r(-1)) / 2 * delta
    // nextPosition() -> f(r2, v2)
    // nextVelocity() -> v = (r2 - r0) / 2 * delta

    private double getNextPosition() {
        double nextPosition = 2 * this.currentPosition - this.previousPosition +
                force.apply(this.currentPosition, this.currentVelocity) * this.timeStep * this.timeStep / (this.mass);

        this.previousPreviousPosition = this.previousPosition;
        this.previousPosition = this.currentPosition;
        this.currentPosition = nextPosition;

        return nextPosition;
    }

    public double getNextVelocity() {
        double nextVelocity = (this.currentPosition - this.previousPreviousPosition) / (2 * this.timeStep);
        this.currentVelocity = nextVelocity;
        return nextVelocity;
    }

    public void nextTimeStep() {
        this.currentTimeStep += this.timeStep;
    }
}
