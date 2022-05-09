package ar.edu.itba.ss.moleculardynamics.matterradiationinteraction;

import java.util.ArrayList;
import java.util.List;

public class MatterParticles {
    private final Particle[][] matrix;
    private final double distanceBetweenParticles;

    public MatterParticles(int particlesPerRow, double distanceBetweenParticles, double charge, double mass) {
        this.matrix = new Particle[particlesPerRow][particlesPerRow];
        this.distanceBetweenParticles = distanceBetweenParticles;

        initialize(distanceBetweenParticles, charge, mass);
    }

    public Particle get(int row, int column) {
        return matrix[row][column];
    }

    public void set(int row, int column, Particle particle) {
        matrix[row][column] = particle;
    }

    private void initialize(double distanceBetweenParticles, double charge, double mass) {
        for (int i = 0; i < matrix.length; i++) {
            charge = -charge;
            for (int j = 0; j < matrix[i].length; j++) {
                charge = -charge;
                matrix[i][j] = new Particle(distanceBetweenParticles * i, distanceBetweenParticles * j, 0 ,0, mass, charge);
            }
        }
    }

    public Vector2D getTotalElectrostaticForce(Particle other) {
        Vector2D totalForce = new Vector2D(0, 0);

        for (Particle[] particleRow : matrix) {
            for (Particle matterParticle : particleRow) {
                totalForce = totalForce.add(other.getElectrostaticForce(matterParticle));
            }
        }

        return totalForce;
    }

    public double getTotalElectrostaticPotentialEnergy(Particle other) {
        double totalPotentialEnergy = 0;

        for (Particle[] particleRow : matrix) {
            for (Particle matterParticle : particleRow) {
                totalPotentialEnergy += other.getElectrostaticPotentialEnergy(matterParticle);
            }
        }

        return totalPotentialEnergy;
    }
}
