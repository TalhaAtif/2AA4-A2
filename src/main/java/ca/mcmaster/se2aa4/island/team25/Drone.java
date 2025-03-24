package ca.mcmaster.se2aa4.island.team25;

import ca.mcmaster.se2aa4.island.team25.Heading;


public class Drone {

    //variable declaration
    private Integer batteryLevel;
    Heading direction;
    LocationPoint currentLocation;
    Heading currentHeading;
    Heading initialHeading;
    public MapRepresenter mapRepresenter;
    Boolean spawnedFacingGround = false;
    //decision making variables:
    String action;

    //drone contructor, will implement the mapping soon
    public Drone(Integer batteryLevel, String initialHeading, MapRepresenter mapRepresenter) {
        this.batteryLevel = batteryLevel;
        this.currentHeading = Heading.valueOf(initialHeading);
        this.initialHeading = Heading.valueOf(initialHeading);
        this.currentLocation = new Points(100, 100); 
        this.mapRepresenter = mapRepresenter;
    }

    //updates current location by one coord, the fly method continues to go in that direction, flys forward
    public String fly() {
        try {
            switch (currentHeading) {
                case N:
                    currentLocation = mapRepresenter.map.get(currentLocation.getRow() - 1).get(currentLocation.getColumn());
                    break;
                case E:
                    currentLocation = mapRepresenter.map.get(currentLocation.getRow()).get(currentLocation.getColumn() + 1);
                    break;
                case S:
                    currentLocation = mapRepresenter.map.get(currentLocation.getRow() + 1).get(currentLocation.getColumn());
                    break;
                case W:
                    currentLocation = mapRepresenter.map.get(currentLocation.getRow()).get(currentLocation.getColumn() - 1);
                    break;
                default:
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            return decisionTaken("stop");
        }
        return decisionTaken("fly");
    }

    //this updates currentHeading and allows for the next heading to be a specific turn 
    public void initializeCurrentLocation(Integer leftColumns, Integer topRows, Boolean spawnedFacingGround) {
        int rows;
        int columns;
        this.spawnedFacingGround = spawnedFacingGround;

        // we didnt change heading and so leftX and topY are the same as the current
        // location
        if (spawnedFacingGround) {
            rows = topRows;
            columns = leftColumns;
        }
        // since we changed heading, leftX and topY are off by a bit, the 100 we
        // intialized currentLocation at (100, 100)
        else {
            rows = topRows + currentLocation.getRow() - 100;
            columns = leftColumns + currentLocation.getColumn() - 100;
        }

        currentLocation = mapRepresenter.map.get(rows).get(columns);
    }

    // this method also updates current location based on current heading and next
    // heading
    public String heading(Heading heading) {
        if (heading == currentHeading || heading == currentHeading.backSide()) {
            throw new IllegalArgumentException("Invalid heading");
        }
        try {
            if (heading == currentHeading.leftSide()) {
                switch (currentHeading) {
                    case N:
                        currentLocation = mapRepresenter.map.get(currentLocation.getRow() - 1)
                                .get(currentLocation.getColumn() - 1);
                        break;
                    case E:
                        currentLocation = mapRepresenter.map.get(currentLocation.getRow() - 1)
                                .get(currentLocation.getColumn() + 1);
                        break;
                    case S:
                        currentLocation = mapRepresenter.map.get(currentLocation.getRow() + 1)
                                .get(currentLocation.getColumn() + 1);
                        break;
                    case W:
                        currentLocation = mapRepresenter.map.get(currentLocation.getRow() + 1)
                                .get(currentLocation.getColumn() - 1);
                        break;
                    default:
                        return null;
                }
            }

            if (heading == currentHeading.rightSide()) {
                switch (currentHeading) {
                    case N:
                        currentLocation = mapRepresenter.map.get(currentLocation.getRow() - 1)
                                .get(currentLocation.getColumn() + 1);
                        break;
                    case E:
                        currentLocation = mapRepresenter.map.get(currentLocation.getRow() + 1)
                                .get(currentLocation.getColumn() + 1);
                        break;
                    case S:
                        currentLocation = mapRepresenter.map.get(currentLocation.getRow() + 1)
                                .get(currentLocation.getColumn() - 1);
                        break;
                    case W:
                        currentLocation = mapRepresenter.map.get(currentLocation.getRow() - 1)
                                .get(currentLocation.getColumn() - 1);
                        break;
                    default:
                        return null;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return decisionTaken("stop");
        }
        return decisionTaken("heading", heading.toString());
    }

    public Heading getDirection() {
        return direction;
    }

    public String echo(Heading heading) {
        return decisionTaken("echo", heading.toString());
    }

    public String scan() {
        return decisionTaken("scan");
    }

    public String stop() {
        return decisionTaken("stop");
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public String getAction() {
        return action;
    }

    public LocationPoint getCurrentLocation() {
        return currentLocation;
    }

    public Heading getCurrentHeading() {
        return currentHeading;
    }
    public void setCurrentHeading(Heading currentHeading) {
        this.currentHeading = currentHeading;
    }

    public void setCurrentLocation(LocationPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Boolean getSpawnedFacingGround() {
        return spawnedFacingGround;
    }


    public void updateBatteryLevel(Integer cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        batteryLevel -= cost;
    }

    String decisionTaken(String command) {

        // ensures the commands are valid
        if (!command.equals("fly") && !command.equals("scan") && !command.equals("stop")) {
            throw new IllegalArgumentException("Invalid command");
        }
        action = command;
        String nextDecision = "{\"action\": \"" + command + "\"}";
        return nextDecision;
    }

    String decisionTaken(String command, String direction) {

        // need to make sure that the commands are valid
        if (!command.equals("echo") && !command.equals("heading")) {
            throw new IllegalArgumentException("Invalid command");
        }
        // ensures the direction is valid
        if (!direction.equals("N") && !direction.equals("E") && !direction.equals("S") && !direction.equals("W")) {
            throw new IllegalArgumentException("Invalid direction");
        }

        // if the command is heading, then the currentDirection is the new heading
        if (command.equals("heading")) {
            this.currentHeading = Heading.valueOf(direction);
        }

        // store the parameters of the next decision
        action = command;
        this.direction = Heading.valueOf(direction);

        String nextDecision = "{\"action\": \"" + command + "\", \"parameters\": { \"direction\": \"" + direction
                + "\"}}";
        return nextDecision;
    }

    

}
