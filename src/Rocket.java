public class Rocket {
    private double position;
    private double velocity;
    private double acceleration;
    private double mass;
    private double fuel;

    public Rocket(double mass, double fuel) {
        this.mass = mass;
        this.fuel = fuel;
        this.position = 0.0;
        this.velocity = 0.0;
        this.acceleration = 0.0;
    }

    // One Euler step: given the net force on the rocket and a small time step dt,
    // update acceleration, then velocity, then position.
    public void step(double netForce, double dt) {
        acceleration = netForce / mass;
        velocity = velocity + acceleration * dt;
        position = position + velocity * dt;
    }

    public void burnFuel(double amount) {
        fuel = fuel - amount;
    }

    public double getPosition() {
        return position;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getMass() {
        return mass;
    }

    public double getFuel() {
        return fuel;
    }
}
