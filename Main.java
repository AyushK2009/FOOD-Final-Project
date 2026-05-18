import java.util.Scanner;

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
                simulation.printConsoleGraph();

                // Here you would add code to display results and graphs based on the collected data in the Simulate class.

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid planet name or fuel amount entered. Please try again.");
                continue; // Restart the loop to allow the user to enter valid input
            }

            System.out.print("Do you want to run another simulation? (yes/no): ");
            String continueInput = scanner.nextLine().toLowerCase();
            if (!continueInput.equals("yes")) {
                simulationRunning = false;
                System.out.println("Thank you for using the Rocket Simulator! Goodbye.");
            }
        }
    }
}
