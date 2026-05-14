public class Classes{
    enum Planet {
        EARTH, MOON, MARS, SPACE;
    }   

    enum Stage {
        ONE, TWO, THREE;
    }

    public void stageCheck(Stage stage) {
        switch(stage) {
            case ONE:
                fThrust = thrustForce;
                fGravity = -m * gravity;
                break;
            case TWO:
                fThrust = 0.0;
                fGravity = 0.0;
                location = Planet.SPACE;
                planetCheck(location);
                break;
            case THREE:
                fThrust = 0.0;
                location = destination;
                planetCheck(location);
                fGravity = m * gravity;
                break;
        }
    }

    public void planetCheck(Planet planet) {
        switch(planet) {
            case EARTH:
                gravity = 9.81;
                airDensity = 1.225;
                break;
            case MOON:
                gravity = 1.62;
                airDensity = 0.0;
                break;
            case MARS:
                gravity = 3.71;
                airDensity = 0.02;
                break;
            case SPACE:
                gravity = 0.0;
                airDensity = 0.0;
                break;
        }
    }
}
