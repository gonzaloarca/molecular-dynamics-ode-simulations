package ar.edu.itba.ss.odemethods;

public interface OdeMethod {
    double[] solve(int steps, double stepSize);
}
