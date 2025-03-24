package ca.mcmaster.se2aa4.island.team25;

import java.util.List;

public class Search {
    Heading generalDirection;
    Heading gridSearchDirection;

    MapRepresenter map;
    Drone drone;

    // initial location and heading of the drone when grid search started
    LocationPoint initialLocation;
    Heading initialHeading;

    int distanceToFly;
    int outOfRangeCounter = 0;

    boolean middle;
    Boolean atEdge = false;
    Boolean translated = false;
    String sideToTurn = "";
    
    public Search(Drone drone, MapRepresenter map){
        this.drone = drone;
        this.initialLocation = drone.getCurrentLocation();
        this.initialHeading = drone.getCurrentHeading();
        this.gridSearchDirection = initialHeading;
        this.map = map;
        this.middle = drone.getSpawnedFacingGround();
        initializeGeneralDirection();
    }

    public void initializeGeneralDirection() {
        if (initialLocation.getRow() < map.rows/2 && initialLocation.getColumn() < map.columns/2) {
            if (initialHeading == Heading.N || initialHeading == Heading.S) {
                generalDirection = Heading.E;
            } else if (initialHeading == Heading.E || initialHeading == Heading.W) {
                generalDirection = Heading.S;
            }
        } else if (initialLocation.getColumn() >= map.columns/2 && initialLocation.getRow() < map.rows/2) {
            if (initialHeading == Heading.N || initialHeading == Heading.S) {
                generalDirection = Heading.W;
            } else if (initialHeading == Heading.E || initialHeading == Heading.W) {
                generalDirection = Heading.S;
            }
        } else if (initialLocation.getRow() >= map.rows/2 && initialLocation.getColumn() < map.columns/2) {
            if (initialHeading == Heading.N || initialHeading == Heading.S) {
                generalDirection = Heading.E;
            } else if (initialHeading == Heading.E || initialHeading == Heading.W) {
                generalDirection = Heading.N;
            }
        } else if (initialLocation.getColumn() >= map.columns/2 && initialLocation.getRow() >= map.rows/2) {
            if (initialHeading == Heading.N || initialHeading == Heading.S) {
                generalDirection = Heading.W;
            } else if (initialHeading == Heading.E || initialHeading == Heading.W) {
                generalDirection = Heading.N;
            }
        }
    }

    public Boolean foundClosestCreek(MapRepresenter map) {
        boolean foundClosestCreek = true;
        double radius = map.getClosestCreekDistance();

        for (List<LocationPoint> pointRow : map.map) {
            for (LocationPoint p : pointRow) {
                double distance = map.distanceBetweenTwoPoints(p, map.getSite());
                if (distance <= radius) {
                    Points normalPoint = (Points) p;
                    if (!normalPoint.getBeenScanned()){
                        foundClosestCreek = false;
                        return foundClosestCreek;
                    }
                }

            }
        }
        return foundClosestCreek;
    }
}
