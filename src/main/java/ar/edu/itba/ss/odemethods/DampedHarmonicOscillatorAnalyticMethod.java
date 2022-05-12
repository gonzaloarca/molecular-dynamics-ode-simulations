package ar.edu.itba.ss.odemethods;

import java.util.function.BiFunction;

public class DampedHarmonicOscillatorAnalyticMethod implements OdeMethod{

    private final double mass;
    private final double amplitude;
    private final double k;
    private final double gamma;
    private double currentTimeStep;

    public DampedHarmonicOscillatorAnalyticMethod(double r0, double v0, double mass, double k, double gamma) {
        this.mass = mass;
        this.k = k;
        this.gamma = gamma;
        this.amplitude = r0;
        this.currentTimeStep = 0;
    }

    private double getNextAnalyticPosition(double stepSize) {
        double nextPosition = amplitude
                * Math.exp(-0.5 * gamma * currentTimeStep / mass)
                * Math.cos(Math.sqrt(k / mass
                - 0.25 * gamma * gamma / (mass * mass)) * currentTimeStep);

        currentTimeStep += stepSize;

        return nextPosition;
    }

    public double[] solve(int steps, double stepSize) {
        double[] positions = new double[steps];
        
        for (int i = 0; i < steps; i++) {
            positions[i] = getNextAnalyticPosition(stepSize);
        }
        
        return positions;
    }

    @Override
    public double getNextVelocity(double stepSize) {
        return 0;
    }

    @Override
    public double getNextPosition(double stepSize) {
        return 0;
    }

}
