public class States {

    // The threshold (in meters) used to decide when state transitions happen.
    // Above this altitude the rocket has "left the atmosphere"; within this
    // distance of the destination it's "entered" the destination's atmosphere.
    public static final double atmosphereTop = 100000.0;

    // Each Planet carries its own physical constants. Looking up
    // EARTH.getGravity() returns 9.8 directly, so we don't need separate
    // lookup tables. (Sources for the values are in Documentation.md.)
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

    // The Stage enum knows how to combine the three forces for its own stage.
    // Simulate just passes in the four ingredients and the right combination
    // comes back out, so we don't need an if-ladder in Simulate.run().
    public enum Stage {
        ONE, TWO, THREE;

        public double calculateForce(double mass, double gravity, double drag, double thrust) {
            if (this == ONE) {
                // Launch: thrust forward, gravity back, drag opposing motion
                return thrust - (mass * gravity) + drag;
            } else if (this == TWO) {
                // Cruise: coast through space, no forces
                return 0.0;
            } else {
                // Landing: gravity pulls toward destination, drag opposes motion
                return (mass * gravity) + drag;
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

    // Called by Simulate every step. Checks whether a state transition should
    // happen given the rocket's current position, and if so, updates the state
    // fields and announces the transition.
    public void updateState(double position, double currentTime) {
        if (stage == Stage.ONE && position > atmosphereTop) {
            stage = Stage.TWO;
            location = Planet.SPACE;
            System.out.println("Reached " + location + " at t = "
                    + (currentTime * displayScale) + " s. Cruise phase started.");
        } else if (stage == Stage.TWO && position > tripDistance - atmosphereTop) {
            stage = Stage.THREE;
            location = destination;
            System.out.println("Approaching " + location + " at t = "
                    + (currentTime * displayScale) + " s. Entering atmosphere.");
        }
    }

    // Getters that Simulate uses to read the current state. Gravity and air
    // density are pulled directly off the current Planet, so they're always
    // in sync with location with no manual update needed.
    public Planet getLocation() {return location; }
    public Planet getDestination() {return destination; }
    public Stage  getStage() {return stage; }
    public double getGravity() {return location.getGravity(); }
    public double getAirDensity() {return location.getAirDensity(); }
    public double getTripDistance() {return tripDistance; }
    public double getDisplayScale() {return displayScale; }

    // Compressed internal trip distances (see Documentation.md for why).
    private double tripDistanceFor(Planet from, Planet to) {
        if (from == Planet.EARTH && to == Planet.MOON)  return 1000000.0;
        if (from == Planet.MOON  && to == Planet.EARTH) return 1000000.0;
        if (from == Planet.EARTH && to == Planet.MARS)  return 5000000.0;
        if (from == Planet.MARS  && to == Planet.EARTH) return 5000000.0;
        if (from == Planet.MOON  && to == Planet.MARS)  return 5000000.0;
        if (from == Planet.MARS  && to == Planet.MOON)  return 5000000.0;
        return 0.0;
    }

    // Per-trip multiplier that converts internal compact units into realistic
    // real-world units for the user-facing output.
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
