package ar.edu.itba.ss.moleculardynamics.matterradiationinteraction;

import ar.edu.itba.ss.odemethods.GearPredictorCorrector;
import ar.edu.itba.ss.odemethods.OdeMethod;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Simulation {
    private final MatterParticles matterParticles;
    private final Particle radiationParticle;
    private final double distanceBetweenParticles;
    private final double boxLength;
    private final double boxHeight;

    public Simulation(int particlesPerRow, double distanceBetweenParticles, double initialHeight, double initialSpeed, double charge, double mass) {
        this.matterParticles = new MatterParticles(particlesPerRow, distanceBetweenParticles, charge, mass);
        this.radiationParticle = new Particle(-distanceBetweenParticles, initialHeight, initialSpeed, 0, mass, charge);
        this.distanceBetweenParticles = distanceBetweenParticles
        this.boxLength = distanceBetweenParticles * particlesPerRow;
        this.boxHeight = this.boxLength - distanceBetweenParticles;
    }

    public boolean isFinished(double dCut) {
        boolean hitYBoundary = Double.compare(radiationParticle.getX(), 0.05) < 0 ||
                Double.compare(radiationParticle.getX(), boxLength) > 0;

        boolean hitXBoundary = Double.compare(radiationParticle.getY(), 0.05) < 0 ||
                Double.compare(radiationParticle.getY(), boxHeight) > 0;

        double xModulus = radiationParticle.getX() % distanceBetweenParticles;
        double yModulus = radiationParticle.getY() % distanceBetweenParticles;

        Vector2D positionInSquare = new Vector2D(xModulus, yModulus);

        double upperLeftDistance = positionInSquare.distance(new Vector2D(0, 0));
        double upperRightDistance = positionInSquare.distance(new Vector2D(distanceBetweenParticles, 0));
        double lowerLeftDistance = positionInSquare.distance(new Vector2D(0, distanceBetweenParticles));
        double lowerRightDistance = positionInSquare.distance(new Vector2D(distanceBetweenParticles, distanceBetweenParticles));

        boolean isAbsorbed = upperLeftDistance < dCut || upperRightDistance < dCut ||
                lowerLeftDistance < dCut || lowerRightDistance < dCut;

        return hitXBoundary || hitYBoundary || isAbsorbed;
    }

    public List<Vector2D>[] solveGear(double stepSize, double steps) {
        Function<Particle, BiFunction<Double, Double, Double>> xForce = (p) -> (r, v) -> matterParticles.getTotalElectrostaticForce(p).x();
        Function<Particle, BiFunction<Double, Double, Double>> yForce = (p) -> (r, v) -> matterParticles.getTotalElectrostaticForce(p).y();

        OdeMethod xSolver = new GearPredictorCorrector(radiationParticle.getX(), radiationParticle.getVx(), 0, 0, 0, xForce.apply(this.radiationParticle), radiationParticle.getMass());
        OdeMethod ySolver = new GearPredictorCorrector(radiationParticle.getY(), radiationParticle.getVy(), 0, 0, 0, yForce.apply(this.radiationParticle), radiationParticle.getMass());

        List<Vector2D> positions = new ArrayList<>();
        List<Vector2D> velocities = new ArrayList<>();

        double dCut = 0.01 * distanceBetweenParticles;

        while(!isFinished(dCut)) {
            double nextXPosition = xSolver.getNextPosition(stepSize);
            double nextYPosition = ySolver.getNextPosition(stepSize);
            double nextXVelocity = xSolver.getNextVelocity(stepSize);
            double nextYVelocity = ySolver.getNextVelocity(stepSize);

            positions.add(new Vector2D(nextXPosition, nextYPosition));
            velocities.add(new Vector2D(nextXVelocity, nextYVelocity));
        }

        return new List[]{positions, velocities};
    }

    public static void printResults(List<Double> results, int saveFrequency, String odeMethod, String fileName) throws IOException {

        int steps = results.size();

        PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));

        printWriter.print(odeMethod +"\n");

        final List<String> currentOutput = new ArrayList<>();

        for (int currentStep = 0; currentStep < steps; currentStep++) {
            if (currentStep % saveFrequency == 0) {

                printWriter.print(results.get(currentStep) + "\n");

            }
        }

        printWriter.close();
    }


}
