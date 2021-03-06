package ar.edu.itba.ss.moleculardynamics.matterradiationinteraction;

import ar.edu.itba.ss.odemethods.GearPredictorCorrector;
import ar.edu.itba.ss.odemethods.OdeMethod;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Simulation {
    private final static String STATIC_FILE_NAME = "static.txt";
    private final static String DYNAMIC_FILE_NAME = "dynamic.txt";
    private final static String MATTER_FILE_NAME = "matter.txt";
    private final static String SUMMARY_FILE_NAME = "summary.txt";
    private final MatterParticles matterParticles;
    private final Particle radiationParticle;
    private final double distanceBetweenParticles;
    private final double boxWidth;
    private final double boxHeight;


    public Simulation(int particlesPerRow, double distanceBetweenParticles, double initialHeight, double initialSpeed, double charge, double mass) {
        this.matterParticles = new MatterParticles(particlesPerRow, distanceBetweenParticles, charge, mass);
        this.radiationParticle = new Particle(-distanceBetweenParticles, initialHeight, initialSpeed, 0, mass, charge);
        this.boxWidth = this.boxHeight = distanceBetweenParticles * (particlesPerRow - 1);
        this.distanceBetweenParticles = distanceBetweenParticles;
    }

    public static void main(String[] args) throws IOException {

        double charge = Math.pow(10, -19);
        double mass = Math.pow(10, -27);
        double distanceBetweenParticles = Math.pow(10, -8);
        int particlesPerRow = 16;
        double L = (particlesPerRow - 1) * distanceBetweenParticles;
        double dt = Math.pow(10, -15);
        int dt2 = 1;
        Random random = new Random();

        double stepSize = Double.parseDouble(System.getProperty("stepSize", Double.toString(dt)));
        int saveFrequency = Integer.parseInt(System.getProperty("saveFrequency", Integer.toString(dt2)));
        double initialSpeed = Double.parseDouble(System.getProperty("initialSpeed", "50000"));
        double initialHeightRatio = Double.parseDouble(System.getProperty("initialHeightRatio", Double.toString(random.nextDouble())));

        double initialHeight = (L / 2 - distanceBetweenParticles) + initialHeightRatio * 2 * distanceBetweenParticles;
        System.out.println("Initial height: " + initialHeight);
        System.out.println("Step size: " + stepSize);

        Simulation simulation = new Simulation(particlesPerRow, distanceBetweenParticles, initialHeight, initialSpeed, charge, mass);

        printStaticData(distanceBetweenParticles, particlesPerRow, L, L, initialHeight, initialSpeed, mass, charge, stepSize, saveFrequency);
        printMatter(simulation.matterParticles);

        simulation.solveGear(stepSize, saveFrequency);

    }

    public static void printResult(Vector2D position, Vector2D velocity, double potentialEnergy, PrintWriter printWriter) {
        printWriter.printf("%.23e %.23e %.23e %.23e %.23e\n", position.x(), position.y(), velocity.x(), velocity.y(), potentialEnergy);
    }

    public static void printMatter(MatterParticles matter) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(MATTER_FILE_NAME));

        for (Particle particle : matter) {
            printWriter.printf("%.23e %.23e %d\n", particle.getX(), particle.getY(), Double.compare(particle.getCharge(), 0.0));
        }

        printWriter.close();
    }

    public static void printStaticData(double distanceBetweenParticles, int particlesPerRow, double boxHeight, double boxWidth, double initialHeight, double initialSpeed, double mass, double charge, double stepSize, int saveFrequency) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(STATIC_FILE_NAME));

        printWriter.printf("%.23e\n%d\n%.23e %.23e\n%.23e\n%.23e\n%.23e\n%.23e\n%.23e\n%d\n", distanceBetweenParticles, particlesPerRow, boxHeight, boxWidth, initialHeight, initialSpeed, mass, charge, stepSize, saveFrequency);

        printWriter.close();
    }

    public static void printSummaryData(SimulationStatus finishReason) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(SUMMARY_FILE_NAME));

        printWriter.print(finishReason);

        printWriter.close();
    }

    public SimulationStatus isFinished(double dCut) {
        boolean hitYLeftBoundary = Double.compare(radiationParticle.getX() + this.distanceBetweenParticles, 0.0) < 0;
        if (hitYLeftBoundary) return SimulationStatus.LEFT_ESCAPED;

        boolean hitYRightBoundary = Double.compare(radiationParticle.getX(), boxWidth) > 0;
        if (hitYRightBoundary) return SimulationStatus.RIGHT_ESCAPED;

        boolean hitXBottomBoundary = Double.compare(radiationParticle.getY(), 0.0) < 0;
        if (hitXBottomBoundary) return SimulationStatus.BOTTOM_ESCAPED;

        boolean hitXTopBoundary = Double.compare(radiationParticle.getY(), boxHeight) > 0;
        if (hitXTopBoundary) return SimulationStatus.TOP_ESCAPED;

        double xModulus = Math.abs(radiationParticle.getX()) % distanceBetweenParticles;
        double yModulus = radiationParticle.getY() % distanceBetweenParticles;

        Vector2D positionInSquare = new Vector2D(xModulus, yModulus);

        // (0,0)
        double upperLeftDistance = positionInSquare.distance(new Vector2D(0, 0));
        // (d, 0)
        double upperRightDistance = positionInSquare.distance(new Vector2D(distanceBetweenParticles, 0));
        // (0, d)
        double lowerLeftDistance = positionInSquare.distance(new Vector2D(0, distanceBetweenParticles));
        // (d, d)
        double lowerRightDistance = positionInSquare.distance(new Vector2D(distanceBetweenParticles, distanceBetweenParticles));

        boolean wasAbsorbed = upperLeftDistance < dCut || upperRightDistance < dCut || lowerLeftDistance < dCut || lowerRightDistance < dCut;

        if(wasAbsorbed) return SimulationStatus.ABSORBED;

        return SimulationStatus.NOT_FINISHED;
    }

    public void solveGear(double stepSize, int saveFrequency) throws IOException {

        Function<Particle, BiFunction<Double, Double, Double>> xForce = (p) -> (r, v) -> matterParticles.getTotalElectrostaticForce(p).x();
        Function<Particle, BiFunction<Double, Double, Double>> yForce = (p) -> (r, v) -> matterParticles.getTotalElectrostaticForce(p).y();

        OdeMethod xSolver = new GearPredictorCorrector(radiationParticle.getX(), radiationParticle.getVx(), 0, 0, 0, xForce.apply(this.radiationParticle), radiationParticle.getMass(),false);
        OdeMethod ySolver = new GearPredictorCorrector(radiationParticle.getY(), radiationParticle.getVy(), 0, 0, 0, yForce.apply(this.radiationParticle), radiationParticle.getMass(),false);

        double dCut = 0.01 * distanceBetweenParticles;

        int currentStep = 0;

        PrintWriter printWriter = new PrintWriter(new FileWriter(DYNAMIC_FILE_NAME));

        Vector2D initialPosition = new Vector2D(radiationParticle.getX(), radiationParticle.getY());
        Vector2D initialVelocity = new Vector2D(radiationParticle.getVx(), radiationParticle.getVy());
        double initialPotentialEnergy = matterParticles.getTotalElectrostaticPotentialEnergy(radiationParticle);

        printResult(initialPosition, initialVelocity, initialPotentialEnergy, printWriter);

        SimulationStatus status;

        while ((status = isFinished(dCut)) == SimulationStatus.NOT_FINISHED) {
            double nextXPosition = xSolver.getNextPosition(stepSize);
            double nextYPosition = ySolver.getNextPosition(stepSize);
            double nextXVelocity = xSolver.getNextVelocity(stepSize);
            double nextYVelocity = ySolver.getNextVelocity(stepSize);

            Vector2D nextPosition = new Vector2D(nextXPosition, nextYPosition);
            Vector2D nextVelocity = new Vector2D(nextXVelocity, nextYVelocity);

            radiationParticle.setPosition(nextPosition);
            radiationParticle.setVelocity(nextVelocity);

            currentStep++;

            if (currentStep % saveFrequency == 0) {
                double potentialEnergy = matterParticles.getTotalElectrostaticPotentialEnergy(radiationParticle);
                printResult(nextPosition, nextVelocity, potentialEnergy, printWriter);
            }

        }

        printSummaryData(status);

        printWriter.close();
    }

}
