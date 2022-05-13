package ar.edu.itba.ss.moleculardynamics.dampedharmonicoscillator;

import ar.edu.itba.ss.odemethods.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Simulation {
    private final List<OdeMethod> methods;

    public Simulation(double r0, double v0, double k, double gamma, double mass) {
        BiFunction<Double, Double, Double> force = (r, v) -> -k * r - gamma * v;

        methods = new ArrayList<>();
        methods.add(new DampedHarmonicOscillatorAnalyticMethod(r0, v0, mass, k, gamma));
        methods.add(new VerletMethod(r0, v0, force, mass));
        methods.add(new BeemanMethod(r0, v0, force, mass));

        double initialR2 = force.apply(r0, v0) / mass;
        double initialR3 = (-k * v0 - gamma * initialR2) / mass;
        double initialR4 = (-k * initialR2 - gamma * initialR3) / mass;
        double initialR5 = (-k * initialR3 - gamma * initialR4) / mass;

        methods.add(new GearPredictorCorrector(r0, v0, initialR3, initialR4, initialR5, force, mass,true));

    }

    public static void main(String[] args) throws IOException {

        String outputFileName = System.getProperty("outputFileName", "output.csv");
        double time = Double.parseDouble(System.getProperty("time", "5"));
        double stepSize = Double.parseDouble(System.getProperty("stepSize", "0.01"));
        int steps = (int) (time / stepSize);

        double r0 = 1;
        double gamma = 100;
        double k = 1e4;
        double mass = 70;
        double v0 = -0.5 * r0 * gamma / mass;
        Simulation simulation = new Simulation(r0, v0, k, gamma, mass);

        List<double[]> results = simulation.simulate(steps, stepSize);

        printResults(results, 1, outputFileName);
    }

    public List<double[]> simulate(int steps, double stepSize) {

        final List<double[]> results = new ArrayList<>();

        for (OdeMethod method : this.methods) {
            results.add(method.solve(steps, stepSize));
        }

        return results;
    }

    public static void printResults(List<double[]> results, int saveFrequency, String fileName) throws IOException {

        int steps = results.get(0).length;

        PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));

        printWriter.print("Analytic;Verlet;Beeman;Gear\n");

        final List<String> currentOutput = new ArrayList<>();

        for (int currentStep = 0; currentStep < steps; currentStep++) {
            if (currentStep % saveFrequency == 0) {

                currentOutput.clear();

                for (double[] result : results) {
                    currentOutput.add(Double.toString(result[currentStep]));
                }

                String output = String.join(";", currentOutput);
                printWriter.print(output + "\n");

            }
        }

        printWriter.close();
    }

    public static void printErrors(List<double[]> results, int saveFrequency, String fileName) throws IOException {

    }
}
