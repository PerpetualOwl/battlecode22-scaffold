package miner_v1;

import battlecode.common.*;
import java.util.Random;


public strictfp class RobotPlayer {


    static int turnCount = 0;
    static int unitsProduced = 00000000;    /** if Archon: 00,00,00,00 (num of miners,
                                                builders, soldiers, and sages built)
                                                if Builder: 00,00 (num of laboratories, watchtowers)**/



    int[] knownmapvision = {};  /** need efficient way to store entire map with what enemy units,
                                rubble levels, ore levels have been seen, and how recently
                                updated they were along with history
                                MUST INCLUDE KNOWN DEVELOPMENTS OF OWN UNITS **/


    static final Random rng = new Random(6147);

    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };


/** @param rc **/

    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        while (true) { /** main loop **/

            turnCount += 1;
            MapLocation me = rc.getLocation();

            runGetVision();

            try {
                switch (rc.getType()) {
                    case ARCHON:     runArchon(rc);  break;
                    case MINER:      runMiner(rc);   break;
                    case SOLDIER:    runSoldier(rc); break;
                    case LABORATORY: runLaboratory(rc); break;
                    case WATCHTOWER: runWatchtower(rc); break;
                    case BUILDER:    runBuilder(rc); break;
                    case SAGE:       runSage(rc); break;
                }
            } catch (GameActionException e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } finally {
                Clock.yield();
            }
        }
    }

    static void runGetVision() throws GameActionException {
        
    }

    static void runArchon(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rng.nextBoolean()) {
            // Let's try to build a miner.
            rc.setIndicatorString("Trying to build a miner");
            if (rc.canBuildRobot(RobotType.MINER, dir)) {
                rc.buildRobot(RobotType.MINER, dir);
                unitsProduced = unitsProduced + 1000000;

            }
        } else {
            // Let's try to build a soldier.
            rc.setIndicatorString("Trying to build a soldier");
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
                unitsProduced = unitsProduced + 100;
            }
        }
    }
    static void runMiner(RobotController rc) throws GameActionException {
        // Remember location of archon that produced me
        if (turnCount == 1) { 
            MapLocation archonProducedMeLocation = rc.getLocation();
            // 49 tiles make a square which is easier to code than the entire vision "circle"
            int[] rubbleVision = new int[49]; // array will have numerical values, will be soon replaced though
            int[] leadVision = new int[49];
            int[] goldVision = new int[49];

        }
        // Mine anything possible around me prioritizing ones that i just moved past
        for (int dx = -1; dx <= 1; dx++) { // mine gold first!! (only from drops)
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation)) {
                    rc.mineGold(mineLocation);
                }
        for (int dx = -1; dx <= 1; dx++) { // then mine lead (need to add priority of tiles later on)
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineLead(mineLocation)) {
                    rc.mineLead(mineLocation);
                }
            }
        }
        // Time to get vision for our next movement!!
        int visiontile = -1;
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                MapLocation visionLocation =  new MapLocation(me.x + dx, me.y + dy);
                if (canSenseLocation(MapLocation visionLocation) == true) {
                    visiontile++;
                    rubbleVision[visiontile] = rc.senseRubble(Maplocation visionLocation);
                    leadVision[visiontile] = rc.senseLead(Maplocation visionLocation);
                    goldVision[visiontile] = rc.senseGold(Maplocation visionLocation);
                    if (canSenseRobotAtLocation(MapLocation visionLocation) == true) {
                        if ( //insert code to detect what type of robot and create actions based on it
                    }
                }
            }
        }

        // need new movement protocol based on vision updates
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    static void runSoldier(RobotController rc) throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            MapLocation toAttack = enemies[0].location;
            if (rc.canAttack(toAttack)) {
                rc.attack(toAttack);
            }
        }

        // Also try to move randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
            System.out.println("I moved!");
        }
    }

    static void runLaboratory(RobotController rc) throws GameActionException {
        
    }

    static void runWatchtower(RobotController rc) throws GameActionException {
        RobotInfo[] nearbyRobots = senseNearbyRobots(MapLocation me, int -1, Team opponent);
        for (int i = 0; i < nearbyRobots.length; i++) {
            nearbyRobots[i]


        }


    }

    static void runBuilder(RobotController rc) throws GameActionException {
        
    }

    static void runSage(RobotController rc) throws GameActionException {
        
    }
}
