package ar.edu.itba.ss.moleculardynamics.matterradiationinteraction;

public class Particle {
    private Vector2D position;
    private Vector2D velocity;
    private final double mass;
    private final double charge;

    public Particle(double x, double y, double vx, double vy, double mass, double charge) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(vx, vy);
        this.mass = mass;
        this.charge = charge;
    }

    public Vector2D getElectrostaticForce(Particle other) {
        double force = Constants.COULOMB_CONSTANT * charge * other.charge / Math.pow(this.distance(other), 3);
        return this.position.subtract(other.position).scale(force);
    }

    public double getElectrostaticPotentialEnergy(Particle other) {
        return Constants.COULOMB_CONSTANT * charge * other.charge / this.distance(other);
    }

    public double getX() {
        return this.position.x();
    }

    public double distance(Particle other) {
        return this.position.distance(other.position);
    }

    public double getY() {
        return this.position.y();
    }

    public double getVx() {
        return this.velocity.x();
    }

    public double getVy() {
        return this.velocity.y();
    }

    public double getMass() {
        return this.mass;
    }

    public double getCharge() {
        return this.charge;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }
}
