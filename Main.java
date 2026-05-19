import java.util.Scanner;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Simulation code goes under here:

        boolean simulationRunning = true;
        while (simulationRunning) {

            System.out.println("Welcome to the Rocket Simulator!");
            System.out.println("Please select a location and destination for your rocket trip.");
            System.out.println("Available planets: EARTH, MOON, MARS");
            System.out.print("Enter location: ");
            String locationInput = scanner.nextLine().toUpperCase();
            System.out.print("Enter destination: ");
            String destinationInput = scanner.nextLine().toUpperCase();
            System.out.print("Enter the amount of fuel for the rocket (in liters): ");
            double fuelInput = scanner.nextDouble();
            scanner.nextLine(); // Consume the newline character left by nextDouble()

            try {
                States.Planet location = States.Planet.valueOf(locationInput);
                States.Planet destination = States.Planet.valueOf(destinationInput);

                States states = new States(location, destination);
                Simulate simulation = new Simulate(states, fuelInput);
                simulation.run();

                /*
                Creating graphs: Swing uses "event-driven programming," something we haven't really covered in class 
                where we mostly do "procedural programming." Basically, instead of calling paintComponent ourselves
                in order, we give the GraphPanel to a JFrame and Swing decides when to draw it. In this case, it's triggered 
                when the window first opens, when it's resized, etc.
                 */ 

                // Open a window with the trajectory graph. DISPOSE_ON_CLOSE keeps the JVM alive so the run-again loop still works after closing it.
                JFrame frame = new JFrame("Rocket Trajectory");
                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.add(new GraphPanel(simulation.getTimes(), simulation.getPositions(), states.getDisplayScale(), simulation.getStage1to2Index(), simulation.getStage2to3Index(), location, destination));
                frame.setVisible(true); // Tells Swing to now call paintComponent in GraphPanel and draw the window

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid planet name or fuel amount entered. Please try again.");
                continue; // Restart the loop to allow the user to enter valid input
            }

            System.out.print("Do you want to run another simulation? (yes/no): ");
            String continueInput = scanner.nextLine().toLowerCase();
            if (!continueInput.equals("yes")) {
                simulationRunning = false;
                System.out.println("Thank you for using the Rocket Simulator! To exit the terminal, make sure you close out the graph window first. Goodbye.");
            }
        }
    }
}
