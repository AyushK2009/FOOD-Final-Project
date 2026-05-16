import java.util.ArrayList;

public class Simulate {

    // Rocket constants
    public static final double rocketMass = 50000.0;     // kg
    public static final double crossArea = 10.0;         // m^2, frontal area
    public static final double dragCoefficient = 0.75;   // unitless
    public static final double thrustForce = 1500000.0;  // Newtons (launch only)
    public static final double burnRate = 5.0;           // liters of fuel per second

    // Simulation timing
    public static final double dt = 0.1;       // internal time step in seconds
    public static final double tMax = 100000;  // safety cap on sim time

    // The simulation owns a rocket and consults a States object for everything
    // related to the state machine (current stage, gravity, air density, etc).
    private Rocket rocket;
    private States states;

    // Data collected each step, used later for graphing and summary
    private ArrayList<Double> times = new ArrayList<Double>();
    private ArrayList<Double> positions = new ArrayList<Double>();
    private ArrayList<Double> velocities = new ArrayList<Double>();
    private ArrayList<Double> accelerations = new ArrayList<Double>();

    // Final outcome boolean flags
    private boolean crashed = false;
    private boolean landed = false;

    public Simulate(States states, double fuel) {
        this.states = states;
        this.rocket = new Rocket(rocketMass, fuel);
    }

    public void run() {
        double t = 0.0;

        // Loop until the rocket has covered the trip distance, or we hit the safety cap (only in case of bug).
        while (rocket.getPosition() < states.getTripDistance() && t < tMax) {
            double v = rocket.getVelocity();
            double m = rocket.getMass();
            double gravity = states.getGravity();
            double airDensity = states.getAirDensity();
            States.Stage stage = states.getStage();

            // We don't need to "manually" redefine drag within the stage loops, since it is already being modified by the airDensity variable
            double fDrag = -0.5 * airDensity * dragCoefficient * crossArea * v * Math.abs(v);

            // The Stage enum knows how its forces combine, so we just hand it
            // the four ingredients and get back the net force for this stage.
            double netForce = stage.calculateForce(m, gravity, fDrag, thrustForce);

            // Fuel is used only during the launch stage, since it's the only stage with thrust
            if (stage == States.Stage.ONE) {
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
            if (rocket.getFuel() <= 0 && stage == States.Stage.ONE) {
                crashed = true;
                System.out.println("Rocket crashed: ran out of fuel before reaching space! Try again :(");
                break;
            }

            // States object checks if a transition should occur.
            states.updateState(rocket.getPosition(), t);

            t = t + dt;
        }

        // If we exited the loop without crashing, the rocket reached the destination
        if (!crashed) {
            landed = true;
            System.out.println("Mission successful! Reached " + states.getDestination()
                    + " at t = " + (t * states.getDisplayScale()) + " s.");
        }

        printSummary(t);
    }

    // Print a summary at the end and a sampled view of the trajectory. All values are scaled up to realistic real-world units for the user to easily interpret
    private void printSummary(double endTime) {
        double scale = states.getDisplayScale();

        System.out.println();
        System.out.println("Simulation Summary");
        System.out.println("Total time:       " + (endTime * scale) + " s");
        System.out.println("Final position:   " + (rocket.getPosition() * scale) + " m");
        System.out.println("Final velocity:   " + rocket.getVelocity() + " m/s"); // The neat thing is that velocity doesn't need to be scaled because scaling both distance and time by the same factor cancels out
        System.out.println("Fuel remaining:   " + rocket.getFuel() + " L");
        if (landed) {
            System.out.println("Status:           Landed on " + states.getDestination());
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
            System.out.println((times.get(i) * scale) + "\t" + (positions.get(i) * scale) + "\t" + velocities.get(i) + "\t" + accelerations.get(i));
        }
    }
}
