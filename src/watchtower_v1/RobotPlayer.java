package Watchtower_v1;

import battlecode.common.*;
import java.util.Random;


public strictfp class RobotPlayer {


    static int turnCount = 0;
    static int unitsProduced = 00000000;    /** if Archon: 00,00,00,00 (num of miners,
                                                builders, soldiers, and sages built)
                                                if Builder: 00,00 (num of laboratories, watchtowers)**/
    static Team ally = getTeam();
    static Team opponent = A;
    if (ally == A) {
        opponent = B;
    }



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
                };
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

    }
    static void runMiner(RobotController rc) throws GameActionException {

    }

    static void runSoldier(RobotController rc) throws GameActionException {
        }
    }

    static void runLaboratory(RobotController rc) throws GameActionException {
        
    }

    static void runWatchtower(RobotController rc) throws GameActionException {
        if (turnCount == 1) {
            Team ally = getTeam();
            Team opponent = A;
            if (ally == A) {
                opponent = B;
            }
        }
        RobotInfo[] nearbyRobots = senseNearbyRobots(MapLocation me, int -1, Team opponent);
        int[] robotvalue = new int[nearbyRobots.length]; // makes targeting value array for each enemy robot
        int rhealth;
        Robottype rtype;
        for (int i = 0; i < nearbyRobots.length; i++) {
            rtype = nearbyRobots[i].get("type"); // gets type of robot

            rhealth = nearbyRobots[i].get("health"); // gets health of robot

            robotvalue[i] = (100 - rhealth); // creating priority valuations
            // going for last hits with priority on higher value units
            if (rtype == SAGE) {
                robotvalue[i] = robotvalue[i] + 200;
            }
            if (rtype == WATCHTOWER) {
                robotvalue[i] = robotvalue[i] + 150;
            }
            if (rtype == ARCHON) {
                robotvalue[i] = robotvalue[i] + 1500;
            }
            if (rtype == SOLDIER) {
                robotvalue[i] = robotvalue[i] + 100;
            }
        }
        int maximum = robotvalue[0];   // find maximum robotvalue and index
        int index = 0;
        for (int i=1; i<robotvalue.length; i++) {
            if (robotvalue[i] > maximum) {
                maximum = robotvalue[i];
                index = i;
            }
        }
        MapLocation rlocation = nearbyRobots[index].get("location");
        if (canAttack(rlocation)) {
            attack(rlocation);
        }

    }

    static void runBuilder(RobotController rc) throws GameActionException {
        if (turnCount == 1) {
            Team ally = getTeam();
            Team opponent = A;
            if (ally == A) {
                opponent = B;
            }

            // three possible statuses - need to decide how to set it
            // 0: target repair status, lock onto a normal building and repair it over and over, apply repair to secondary target if primary target is full hp
            // 1: laboratory build/repair status, use recommended coordinates saved and provided (probably recorded) to build specific building
            // 1: same as above but for turrets
            int builderStatus = 0;
            MapLocation currentTargetLocation; //goal lab location, goal watchtower prototype location or defending a target
        }
        // INSERT FUNCTION/COMMUNICATION TO DETERMINE THE THREE ABOVE VARIABLES

        if (builderStatus == 0){
            // first try to repair target, if it doesn't need healing, then search for other viable targets
            RobotInfo[] nearbyRobots = senseNearbyRobots(MapLocation me, int -1, Team ally);
            int rhealth;
            Robottype rtype;
            int[] robotvalue = new int[nearbyRobots.length];
            int turns = getActionCooldownTurns();
            if ((canRepair(currentTargetLocation)) && (turns == 0)) {
                rtype = nearbyRobots[i].get("type");
                rhealth = nearbyRobots[i].get("health");
                if ((rtype == WATCHTOWER) && (rhealth < 110)) {
                    repair(currentTargetLocation);
                } else if ((rtype == ARCHON) && (rhealth < 950)) {
                    repair(currentTargetLocation);
                } else if ((rtype == LABORATORY) && (rhealth < 80)) {
                    repair(currentTargetLocation);
                }
            } else if (turns == 0) {
                for (int i = 0; i < nearbyRobots.length; i++) {
                    rtype = nearbyRobots[i].get("type"); // gets type of robot
                    rhealth = nearbyRobots[i].get("health"); // gets health of robot
                    if ((rtype == WATCHTOWER)) {
                        robotvalue[i] = 100 - rhealth
                    } else if ((rtype == ARCHON)) {
                        robotvalue[i] = 1300 - rhealth
                    } else if ((rtype == LABORATORY)) {
                        robotvalue[i] = 10 - rhealth                    }
                }
                int maximum = robotvalue[0];   // find maximum robotvalue and index
                int index = 0;
                for (int i=1; i<robotvalue.length; i++) {
                    if (robotvalue[i] > maximum) {
                        maximum = robotvalue[i];
                        index = i;
                    }
                }
                MapLocation rlocation = nearbyRobots[index].get("location");
                if (canRepair(rlocation)) {
                    repair(rlocation);
                }
            }
        } else if (((builderStatus == 1) || (builderStatus == 2)) && (canSenseLocation(currentTargetLocation))) {
            // if builder is actively pathfinding to currentTargetLocation, this part of the code can be ignored
            // if builder is within range of currentTargetLocation, check what is there
                //  if there is a completed full hp building, switch to mode 0 with the same target location until overridden
                // if there is a partial hp prototype, repair it
                // if there is nothing there, place a prototype
            RobotInfo targetRobot = senseRobotAtLocation(currentTargetLocation);
            




        }

    }

    static void runSage(RobotController rc) throws GameActionException {
        
    }
}




