package ar.edu.itba.ss.moleculardynamics.matterradiationinteraction;

public record Vector2D(double x, double y) {
    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector2D scale(double factor) {
        return new Vector2D(this.x * factor, this.y * factor);
    }

    public double length() {
        return Math.sqrt(this.dot(this));
    }

    public double distance(Vector2D other) {
        return this.subtract(other).length();
    }

    public double angle(Vector2D other) {
        double angle =  Math.acos(this.dot(other)/ (this.length() * other.length()));
        if(this.x * other.y - this.y * other.x < 0) {
            angle = -angle;
        }
        return angle;
    }
}