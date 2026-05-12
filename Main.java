import java.util.Scanner;

public class Main {
    enum Planet {
        EARTH, MOON, MARS, SPACE;
    }   

    enum Stage {
        ONE, TWO, THREE;
    }
    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the Space Travel Simulator!");
        System.out.println("Please select your location:");
        System.out.println("1. Earth");
        System.out.println("2. Moon");
        System.out.println("3. Mars");
        int locationChoice = sc.nextInt();
        
        double fuel = 0.0; 
        Planet location = Planet.EARTH;
        Planet destination = Planet.MARS;

        if (locationChoice == 1) {
            System.out.println("You have selected Earth.");
            location = Planet.EARTH;
        } else if (locationChoice == 2) {
            System.out.println("You have selected the Moon.");
            location = Planet.MOON;
        } else if (locationChoice == 3) {
            System.out.println("You have selected Mars.");
            location = Planet.MARS;
        }

        System.out.println("Please select your destination:");
        System.out.println("1. Earth");
        System.out.println("2. Moon");
        System.out.println("3. Mars");
        int destinationChoice = sc.nextInt();


        if (destinationChoice == 1) {
            System.out.println("You have selected Earth.");
            destination = Planet.EARTH;
        } else if (destinationChoice == 2) {
            System.out.println("You have selected the Moon.");
            destination = Planet.MOON;
        } else if (destinationChoice == 3) {
            System.out.println("You have selected Mars.");
            destination = Planet.MARS;
        }

        System.out.println("Please enter the amount of fuel you have (in liters):");
        fuel = sc.nextDouble();

        Stage stage = Stage.ONE;

        double ForceAir = 0;
        double Grav = 0;

        switch(location) {
            case EARTH:
                System.out.println("You are on Earth.");
                // Gravity, air resist,
                Grav = 9.8;
                ForceAir = 0.1;
                break;
            case MOON:
                System.out.println("You are on the Moon.");
                // Gravity, air resist,
                Grav = 1.6;
                ForceAir = 0;
                break;
            case MARS:
                System.out.println("You are on Mars.");
                // Gravity, air resist,
                Grav = 3.7;
                ForceAir = 0.01;
                break;
            case SPACE:
                System.out.println("You are in Space.");
                Grav = 0;
                ForceAir = 0;
                break;
        }

                // Might not be needed, but just in case
        switch(stage) {
            case ONE:
                System.out.println("Stage One: Launch");
                break;
            case TWO:
                System.out.println("Stage Two: In-Flight");
                break;
            case THREE:
                System.out.println("Stage Three: Landing");
                break;
        }






        // Simulation code goes under here:


    }
}
