package ar.edu.itba.ss.moleculardynamics.matterradiationinteraction;

import java.util.Iterator;

public class MatterParticles implements Iterable<Particle> {
    private final Particle[][] matrix;
    private final double distanceBetweenParticles;
    private final int particlesPerRow;

    public MatterParticles(int particlesPerRow, double distanceBetweenParticles, double charge, double mass) {
        this.particlesPerRow = particlesPerRow;
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
        double L = distanceBetweenParticles * (particlesPerRow - 1);

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

    public Iterator<Particle> iterator() {
        return new MatterParticlesIterator(matrix, particlesPerRow);
    }

    private class MatterParticlesIterator implements Iterator<Particle> {

        private int row;
        private int column;
        private int particlesPerRow;
        private Particle[][] matrix;

        MatterParticlesIterator(Particle[][] matterParticles, int particlesPerRow) {
            this.matrix = matterParticles;
            this.row = 0;
            this.column = 0;
            this.particlesPerRow = particlesPerRow;
        }

        @Override
        public boolean hasNext() {
            return row < particlesPerRow && column < particlesPerRow;
        }

        @Override
        public Particle next() {
            Particle particle = matrix[row][column];
            if (column == particlesPerRow - 1) {
                row++;
                column = 0;
            } else {
                column++;
            }
            return particle;
        }
    }
}
