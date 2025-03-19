package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONArray;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();

    private JSONObject checker = new JSONObject(); // to store values to check for iteration
    private JSONObject newResponse = new JSONObject();
    private Integer distToGround = 0;
    private Boolean foundLand = false;
    private Boolean boilerPlate = true;
    private Boolean flyCheck = false;
    private Boolean end = false;
    private String action;
    private Boolean boilerPlateMission = true;

    Drone drone;
    MapRepresenter map;
    MissionControl control;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));

        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");

        map = new MapRepresenter();
        drone = new Drone(batteryLevel, direction, map);
        control = new MissionControl(drone, map);

        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        JSONObject parameters = new JSONObject();

        if (boilerPlate) {
            parameters.put("direction", "S");
            decision.put("action", "echo");
            action = "echo";
            decision.put("parameters", parameters);
            boilerPlate = false;
            logger.info("** Decision: {}", decision.toString());
        }

        if (!flyCheck && !foundLand) {
            switch (checker.getJSONObject("check").getString("found")) {
                case "OUT_OF_RANGE":

                    decision.put("action", "fly");
                    action = "fly";
                    flyCheck = true;
                    logger.info("** Decision: {}", decision.toString());
                    return decision.toString();
                case "GROUND":
                    distToGround = checker.getJSONObject("check").getInt("range");
                    foundLand = true;

                    // change heading to south here so we can fly towards ground
                    parameters.put("direction", "S");
                    decision.put("action", "heading");
                    decision.put("parameters", parameters);
                    action = "heading";
                    logger.info("** Decision: {}", decision.toString());
                    return decision.toString();
                default:
                    break;
            }
        } else if (flyCheck) {
            //echo south again so we can see if there is ground in that direction, only do this if the previous echo was out of range (flyCheck = true)
            parameters.put("direction", "S");
            decision.put("action", "echo");
            decision.put("parameters", parameters);
            action = "echo";
            flyCheck = false;// cechl
        }

        if (foundLand && distToGround != 0) {
            // fly south until we reach the ground
            decision.put("action", "fly");
            action = "fly";
            distToGround--;
        }
        if (distToGround == 0 && foundLand && !end) {
            // once we found the ground, we need to scan it so that is shows up on the svg map
            // parameters.put("direction", "S");
            decision.put("action", "scan");
            action = "scan";
            // decision.put("parameters", parameters);
            end = true;
            logger.info("** Decision: {}", decision.toString());
            return decision.toString();
        }

        if (end) {
            // Get the JSONArray from the JSONObject
            JSONArray biomesArray = checker.getJSONObject("check").getJSONArray("biomes");

            // Initialize a String array with the same length as the JSONArray
            String[] biomes = new String[biomesArray.length()];

            // Iterate through the JSONArray and extract each element as a String
            for (int i = 0; i < biomesArray.length(); i++) {
                biomes[i] = biomesArray.getString(i);
            }

            return decision.toString(); 

        }

        if (end) {
            // ground has been found so return to base
            decision.put("action", "stop");
            action = "stop";

        }

        //logger.info("** Decision: {}", decision.toString());
        //decision.put("action", "stop"); // we stop the exploration immediately
        logger.info("** Decision: {}",decision.toString());
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        if (boilerPlateMission) {
            logger.info("Mission has started");
            logger.info("Mission has started");
            boilerPlateMission = false;
        }
        control.startMission(action, response);
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }
    public static void main(String[] args) {
        Explorer e = new Explorer();
        e.initialize("{\"budget\":1000,\"heading\":\"N\"}");
        e.takeDecision();
        e.acknowledgeResults("{\"cost\":1,\"status\":\"success\",\"extras\":{\"range\":1}}");
        e.deliverFinalReport();
    }
}
