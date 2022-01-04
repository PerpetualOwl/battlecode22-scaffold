package examplefuncsplayer;

import battlecode.common.*;
import java.util.Random;


public strictfp class RobotPlayerMicroTest {


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
            System.out.println("Age: " + turnCount + "; Location: " + rc.getLocation());

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
        // Try to mine on squares around us.
        MapLocation me = rc.getLocation();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                // Notice that the Miner's action cooldown is very low.
                // You can mine multiple times per turn!
                while (rc.canMineGold(mineLocation)) {
                    rc.mineGold(mineLocation);
                }
                while (rc.canMineLead(mineLocation)) {
                    rc.mineLead(mineLocation);
                }
            }
        }

        // Also try to move randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
            System.out.println("I moved!");
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
        
    }

    static void runBuilder(RobotController rc) throws GameActionException {
        
    }

    static void runSage(RobotController rc) throws GameActionException {
        
    }
}
