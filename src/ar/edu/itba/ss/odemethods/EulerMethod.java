package ar.edu.itba.ss.odemethods;

import java.util.function.BiFunction;

public class EulerMethod implements OdeMethod {

    private final BiFunction<Double, Double, Double> force;
    private final double mass;
    private double currentPosition;
    private double currentVelocity;


    public EulerMethod(double r0, double v0, BiFunction<Double, Double, Double> force, double mass) {
        this.currentPosition = r0;
        this.currentVelocity = v0;
        this.force = force;
        this.mass = mass;
    }

    @Override
    public double getNextPosition(double stepSize) {
        double nextPosition = currentPosition
                + stepSize * currentVelocity
                + 0.5 * stepSize * stepSize * force.apply(currentPosition, currentVelocity) / mass;

        this.currentPosition = nextPosition;

        return nextPosition;
    }

    @Override
    public double getNextVelocity(double stepSize) {
        double nextVelocity = currentVelocity + stepSize * force.apply(currentPosition, currentVelocity) / mass;

        this.currentVelocity = nextVelocity;

        return nextVelocity;
    }

    public void update(double newPosition, double newVelocity) {
        this.currentPosition = newPosition;
        this.currentVelocity = newVelocity;
    }

    @Override
    public double[] solve(int steps, double stepSize) {
        double[] positions = new double[steps];

        positions[0] = currentPosition;

        for (int i = 1; i < steps; i++) {
            positions[i] = getNextPosition(stepSize);
            getNextVelocity(stepSize);
        }

        return positions;
    }
}
