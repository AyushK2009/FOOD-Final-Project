public class States {

    public static final double atmosphereTop = 100000.0; // m, decides when state transitions happen

    // Each Planet is assigned its own physical constants accoridng to the accepted physics values
    public enum Planet {
        EARTH(9.8, 1.225),
        MOON(1.6, 0.0),
        MARS(3.7, 0.020),
        SPACE(0.0, 0.0);

        private final double gravity;
        private final double airDensity;

        Planet(double gravity, double airDensity) {
            this.gravity = gravity;
            this.airDensity = airDensity;
        }

        public double getGravity() {return gravity; }
        public double getAirDensity() {return airDensity; }
    }

    public enum Stage {
        ONE, TWO, THREE; // Liftoff, cruise, landing

        // Calculates net force to be used within simulation. This way, simulation doesn't need a bunch of if statements checking the stage and corresponding force
        public double calculateForce(double mass, double gravity, double drag, double thrust) {
            switch (this) {
                case ONE:
                    // Launch: thrust forward, gravity back, drag back
                    return thrust - (mass * gravity) + drag;
                case TWO:
                    // Cruise: no forces in space
                    return 0.0;
                case THREE:
                    // Landing: gravity pulls toward destination, drag opposes motion
                    return (mass * gravity) + drag;
                default:
                    return 0.0;
            }
        }
    }

    // The current state of the mission.
    private Planet location;
    private Planet destination;
    private Stage stage;

    // Per-trip values that are fixed once and never change.
    private double tripDistance;
    private double displayScale;

    public States(Planet location, Planet destination) {
        this.location = location;
        this.destination = destination;
        this.stage = Stage.ONE;
        this.tripDistance = tripDistanceFor(location, destination);
        this.displayScale = displayScaleFor(location, destination);
    }

    // Called by Simulate every time step. Checks whet
    public void updateState(double position, double currentTime) {
        // Takeoff to Crusie transition
        if (stage == Stage.ONE && position > atmosphereTop) {
            stage = Stage.TWO;
            location = Planet.SPACE;
            System.out.println("Reached " + location + " at t = "
                    + (currentTime * displayScale) + " s. Cruise phase started.");
        // Cruise to Landing transition
        } else if (stage == Stage.TWO && position > tripDistance - atmosphereTop) {
            stage = Stage.THREE;
            location = destination;
            System.out.println("Approaching " + location + " at t = "
                    + (currentTime * displayScale) + " s. Entering atmosphere.");
        }
    }

    // Getters
    public Planet getLocation() {return location; }
    public Planet getDestination() {return destination; }
    public Stage  getStage() {return stage; }
    public double getGravity() {return location.getGravity(); }
    public double getAirDensity() {return location.getAirDensity(); }
    public double getTripDistance() {return tripDistance; }
    public double getDisplayScale() {return displayScale; }

    // Compressed internal trip distances
    private double tripDistanceFor(Planet from, Planet to) {
        if (from == Planet.EARTH && to == Planet.MOON)  return 1000000.0;
        if (from == Planet.MOON  && to == Planet.EARTH) return 1000000.0;
        if (from == Planet.EARTH && to == Planet.MARS)  return 5000000.0;
        if (from == Planet.MARS  && to == Planet.EARTH) return 5000000.0;
        if (from == Planet.MOON  && to == Planet.MARS)  return 5000000.0;
        if (from == Planet.MARS  && to == Planet.MOON)  return 5000000.0;
        return 0.0;
    }

    // Per-trip multiplier that converts the scaled down time and position into realistic (t, pos) cooridnates
    private double displayScaleFor(Planet from, Planet to) {
        if (from == Planet.EARTH && to == Planet.MOON)  return 384.0;
        if (from == Planet.MOON  && to == Planet.EARTH) return 384.0;
        if (from == Planet.EARTH && to == Planet.MARS)  return 16000.0;
        if (from == Planet.MARS  && to == Planet.EARTH) return 16000.0;
        if (from == Planet.MOON  && to == Planet.MARS)  return 16000.0;
        if (from == Planet.MARS  && to == Planet.MOON)  return 16000.0;
        return 1.0;
    }
}
