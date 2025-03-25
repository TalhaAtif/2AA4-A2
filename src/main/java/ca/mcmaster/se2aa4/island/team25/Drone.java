package ca.mcmaster.se2aa4.island.team25;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Drone {

    private final Logger logger = LogManager.getLogger();

    private Direction direction;
    private Battery battery;
    private Coordinate currentCord;

    private int turnCounter = -1;
    private int perfectTurn = -1;


    public Drone(int battery, char heading) {
        this.currentCord = new Coordinate(0, 0);
        this.direction = Direction.charToDir(heading);
        this.battery = new Battery(battery);

    }

    public boolean goHome() {
        int currentBatt = this.battery.currentBattery();
        int batteryToHome = ((this.currentCord.getX() + this.currentCord.getY()) * 4);
        return (currentBatt < batteryToHome);
    }

    public boolean mustStop() {
        return (battery.currentBattery() < 22);
    }

    public int northToHome() {
        return currentCord.getY();
    }

    public int westToHome() {
        return currentCord.getX();
    }

    public JSONObject createDirectionAction(String actionType, Direction dir) {
        JSONObject action = new JSONObject();
        JSONObject parameters = new JSONObject();

        action.put("action", actionType);
        parameters.put("direction", dir.getIcon());
        action.put("parameters", parameters);

        return action;
    }

    public JSONObject radarDirection(Direction dir) {
        return createDirectionAction("echo", dir);
    }

    public JSONObject turnLeft() {
        this.currentCord.changeCord(direction);
        this.currentCord.changeCord(direction.turnLeft());
        this.direction = this.direction.turnLeft();
        return createDirectionAction("heading", this.direction);
    }

    public JSONObject turnRight() {
        this.currentCord.changeCord(direction);
        this.currentCord.changeCord(direction.turnRight());
        this.direction = this.direction.turnRight();
        return createDirectionAction("heading", this.direction);
    }


    public int getX() {
        return this.currentCord.getX();
    }

    public int getY() {
        return this.currentCord.getY();
    }

    public JSONObject simpleAction(Action type) {
        if (type == Action.FLY) {
            this.currentCord.changeCord(this.direction);
        }
        JSONObject action = new JSONObject();
        action.put("action", type.term);
        return action;
    }

    public Direction currentDir() {
        return this.direction;
    }

    public void batteryLost(int cost) {
        battery.lowerBattery(cost);
    }

}
