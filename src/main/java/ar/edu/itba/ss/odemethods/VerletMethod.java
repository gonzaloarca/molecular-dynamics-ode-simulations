package ar.edu.itba.ss.odemethods;

import java.util.function.BiFunction;

public class VerletMethod implements OdeMethod{
    private final BiFunction<Double, Double, Double> force;
    private final double mass;
    private double previousPreviousPosition;
    private double previousPosition;
    private double currentPosition;
    private double previousVelocity;
    private double currentVelocity;

    public VerletMethod(double r0, double v0, BiFunction<Double, Double, Double> force, double mass) {
        this.force = force;
        this.mass = mass;
        this.currentPosition = r0;
        this.currentVelocity = v0;
        this.previousVelocity = v0;
    }

    @Override
    public double getNextPosition(double stepSize) {
        double nextPosition = 2 * currentPosition - previousPosition +
                force.apply(currentPosition, previousVelocity) * stepSize * stepSize / (mass);

        this.previousPreviousPosition = this.previousPosition;
        this.previousPosition = this.currentPosition;
        this.currentPosition = nextPosition;

        return nextPosition;
    }

    @Override
    public double getNextVelocity(double stepSize) {
        double nextVelocity = (currentPosition - previousPreviousPosition) / (2 * stepSize);

        this.previousVelocity = this.currentVelocity;
        this.currentVelocity = nextVelocity;

        return nextVelocity;
    }

    public double[] solve(int steps, double stepSize) {
        // use Euler's method as an approximation to get r(-dt)
        EulerMethod euler = new EulerMethod(currentPosition, currentVelocity, force, mass);
        this.previousPosition = euler.getNextPosition(-stepSize);

        euler.update(this.previousPosition, euler.getNextVelocity(-stepSize));

        double[] positions = new double[steps];

        positions[0] = this.currentPosition;

        for (int i = 1; i < steps; i++) {
            positions[i] = getNextPosition(stepSize);
            getNextVelocity(stepSize);
        }

        return positions;
    }

}
