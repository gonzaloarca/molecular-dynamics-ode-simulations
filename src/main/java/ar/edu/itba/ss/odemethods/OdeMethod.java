package ar.edu.itba.ss.odemethods;

import java.util.function.Predicate;

public interface OdeMethod {
    double[] solve(int steps, double stepSize);
    double getNextPosition(double stepSize);

    double getNextVelocity(double stepSize);
}
