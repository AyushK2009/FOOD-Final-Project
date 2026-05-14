import java.util.ArrayList;

public class Simulate {

    // Rocket constants
    public static final double rocketMass = 50000.0;     // kg
    public static final double crossArea = 10.0;         // m^2, frontal area
    public static final double dragCoefficient = 0.75;   // unitless
    public static final double thrustForce = 1500000.0;  // Newtons (launch only)
    public static final double burnRate = 5.0;           // liters of fuel per second
    public static final double atmosphereTop = 100000.0; // m

    // Simulation timing
    public static final double dt = 0.1;       // internal time step in seconds
    public static final double tMax = 100000;  // safety cap on sim time    

    // State-based variables
    private Main.Planet location;
    private Main.Planet destination;
    private Main.Stage stage;
    private Rocket rocket;

    private double gravity;      // m/s^2, magnitude of current planet's gravity
    private double airDensity;   // kg/m^3, current planet's air density
    private double tripDistance; // meters, total distance for this trip (internal units)
    private double displayScale; // multiplier to convert internal pos/time -> real-world units

    // Data collected each step, used later for graphing and summary
    private ArrayList<Double> times = new ArrayList<Double>();
    private ArrayList<Double> positions = new ArrayList<Double>();
    private ArrayList<Double> velocities = new ArrayList<Double>();
    private ArrayList<Double> accelerations = new ArrayList<Double>();

    // Final outcome boolean flags
    private boolean crashed = false;
    private boolean landed = false;

    public Simulate(Main.Planet location, Main.Planet destination, double fuel,
                    double startGravity, double startAirDensity) {
        this.location = location;
        this.destination = destination;
        this.stage = Main.Stage.ONE;
        this.rocket = new Rocket(rocketMass, fuel);
        this.gravity = startGravity;
        this.airDensity = startAirDensity;
        this.tripDistance = tripDistanceFor(location, destination);
        this.displayScale = displayScaleFor(location, destination);
    }

    public void run() {
        double t = 0.0;

        // Loop until the rocket has covered the trip distance, or we hit the safety cap (only in case of bug).
        while (rocket.getPosition() < tripDistance && t < tMax) {
            double v = rocket.getVelocity();
            double m = rocket.getMass();

            // We don't need to "manually" redefine drag within the stage loops, since it is already being modified by the airDensity variable
            double fDrag = -0.5 * airDensity * dragCoefficient * crossArea * v * Math.abs(v);
            
            // These forces will be different depdening on which stage we're in
            double fGravity;
            double fThrust;

            if (stage == Main.Stage.ONE) {
                // Launch stage: the thrust pushes rocket away from surface, while the force of gravity and (sometimes) drag pulls it back
                fThrust = thrustForce;
                fGravity = -m * gravity;
            } else if (stage == Main.Stage.TWO) {
                // Cruise stage: coast through space, no forces
                fThrust = 0.0;
                fGravity = 0.0;
            } else {
                // Landing stage: engine remains off, but destination's gravity pulls rocket toward the surface while (sometimes) drag opposes motion.
                fThrust = 0.0;
                fGravity = m * gravity;
            }

            double netForce = fThrust + fGravity + fDrag;

            // Fuel is used only during the launch stage, since it's the only stage with thrust
            if (stage == Main.Stage.ONE) {
                rocket.burnFuel(burnRate * dt);
            }

            // Step the kinematic variables incrementally.
            rocket.step(netForce, dt);

            // Add the new variables to our lists for graphing and results interpretation later
            times.add(t);
            positions.add(rocket.getPosition());
            velocities.add(rocket.getVelocity());
            accelerations.add(rocket.getAcceleration());

            // Fuel check: fuel only burns in stage 1, so this only triggers here
            if (rocket.getFuel() <= 0 && stage == Main.Stage.ONE) {
                crashed = true;
                System.out.println("Rocket crashed: ran out of fuel before reaching space! Try again :(");
                break;
            }

            // State transition thresholds
            if (stage == Main.Stage.ONE && rocket.getPosition() > atmosphereTop) {
                stage = Main.Stage.TWO;
                location = Main.Planet.SPACE;
                gravity = 0.0;
                airDensity = 0.0;
                System.out.println("Reached " + location + " at t = " + (t * displayScale) + " s. Cruise phase started.");
            } else if (stage == Main.Stage.TWO && rocket.getPosition() > tripDistance - atmosphereTop) {
                stage = Main.Stage.THREE;
                location = destination;
                gravity = gravityFor(location);
                airDensity = airDensityFor(location);
                System.out.println("Approaching " + location + " at t = " + (t * displayScale) + " s. Entering atmosphere.");
            }

            t = t + dt;
        }

        // If we exited the loop without crashing, the rocket reached the destination
        if (!crashed) {
            landed = true;
            System.out.println("Mission successful! Reached " + destination
                    + " at t = " + (t * displayScale) + " s.");
        }

        printSummary(t);
    }

    // Helper methods that define the per-planet constants defined by the stage

    // Look up gravity for a given planet.
    private double gravityFor(Main.Planet p) {
        if (p == Main.Planet.EARTH) return 9.8;
        if (p == Main.Planet.MOON)  return 1.6;
        if (p == Main.Planet.MARS)  return 3.7;
        return 0.0; // SPACE
    }

    // Look up air density for a given planet.
    private double airDensityFor(Main.Planet p) {
        if (p == Main.Planet.EARTH) return 1.225;
        if (p == Main.Planet.MOON)  return 0.0;
        if (p == Main.Planet.MARS)  return 0.020;
        return 0.0; // SPACE
    }

    // We scale down trip distance because real values would be much larger. The displayScale converts these back to realistic distances at print time
    private double tripDistanceFor(Main.Planet from, Main.Planet to) {
        if (from == Main.Planet.EARTH && to == Main.Planet.MOON)  return 1000000.0;
        if (from == Main.Planet.MOON  && to == Main.Planet.EARTH) return 1000000.0;
        if (from == Main.Planet.EARTH && to == Main.Planet.MARS)  return 5000000.0;
        if (from == Main.Planet.MARS  && to == Main.Planet.EARTH) return 5000000.0;
        if (from == Main.Planet.MOON  && to == Main.Planet.MARS)  return 5000000.0;
        if (from == Main.Planet.MARS  && to == Main.Planet.MOON)  return 5000000.0;
        return 0.0;
    }

    // The values here scale up the final time and distance according to the takeoff/landing locations, so the output and graphs look realistic.
    // Velocity is left unscaled because scaling both distance and time by the
    // same factor cancels out (v = pos/time).
    private double displayScaleFor(Main.Planet from, Main.Planet to) {
        if (from == Main.Planet.EARTH && to == Main.Planet.MOON)  return 384.0;
        if (from == Main.Planet.MOON  && to == Main.Planet.EARTH) return 384.0;
        if (from == Main.Planet.EARTH && to == Main.Planet.MARS)  return 16000.0;
        if (from == Main.Planet.MARS  && to == Main.Planet.EARTH) return 16000.0;
        if (from == Main.Planet.MOON  && to == Main.Planet.MARS)  return 16000.0;
        if (from == Main.Planet.MARS  && to == Main.Planet.MOON)  return 16000.0;
        return 1.0;
    }

    // Print a summary at the end + a sampled view of the trajectory. All position
    // and time values are scaled to realistic real-world units for the user.
    private void printSummary(double endTime) {
        System.out.println();
        System.out.println("--- Simulation Summary ---");
        System.out.println("Total time:       " + (endTime * displayScale) + " s");
        System.out.println("Final position:   " + (rocket.getPosition() * displayScale) + " m");
        System.out.println("Final velocity:   " + rocket.getVelocity() + " m/s"); // The neat thing is that velocity doesn't need to be scaled because scaling both distance and time by the same factor cancels out
        System.out.println("Fuel remaining:   " + rocket.getFuel() + " L");
        if (landed) {
            System.out.println("Status:           Landed on " + destination);
        } else if (crashed) {
            System.out.println("Status:           Crashed (out of fuel)");
        } else {
            System.out.println("Status:           Timed out. Check for errors");
        }

        // Print every Nth trajectory sample so the user can eyeball the motion.
        System.out.println();
        System.out.println("Trajectory Samples (every ~5% of journey)");
        System.out.println("t (s)\tpos (m)\tvel (m/s)\tacc (m/s^2)");
        int interval = times.size() / 20;
        if (interval < 1) interval = 1;
        for (int i = 0; i < times.size(); i = i + interval) {
            System.out.println((times.get(i) * displayScale) + "\t" + (positions.get(i) * displayScale) + "\t" + velocities.get(i) + "\t" + accelerations.get(i));
        }
    }
}
