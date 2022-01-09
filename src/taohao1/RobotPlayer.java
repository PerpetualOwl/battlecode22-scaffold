package taohao1;
import battlecode.common.*;
import java.lang.Math;

import java.security.cert.TrustAnchor;
import java.util.Random;


/**
NOTES ABOUT COMMUNICATON ARRAY
0-3, enemy archon tracking information _ (1=nonexistent, 0=coords are correct) _ _ (x coord) _ _ (y coord), all in base 10
4-7, allied archon tracking information (first few turns, can get overwritten later)







**/
public strictfp class RobotPlayer {
    static int turnCount = 0;
    static final Random rng = new Random(6147);
    static int minernumber=0;
    static int rubbleVision = 0;
    static int leadVision = 0;
    static int robotAge=1;
    static int robotAge2=1;
    static int goldVision = 0;
    static int minerCount = 0;

    static MapLocation archonloc0;
    static MapLocation archonloc1;
    static MapLocation archonloc2;
    static MapLocation archonloc3;
    static MapLocation archonloc4;
    static MapLocation archonloc5;
    static MapLocation archonloc6;
    static MapLocation archonloc7;
    static MapLocation archonloc8;
    static MapLocation archonloc9;

    static final Direction[] directions = 
    {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.CENTER,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };
    @SuppressWarnings("used")
    public static void run(RobotController rc) throws GameActionException 
    {
        while (true) 
        {
            Team ally = rc.getTeam();
            Team opponent = ally.opponent();
            try 
            {
                switch (rc.getType()) 
                {
                    case ARCHON:     runArchon(rc);  break;
                    case MINER:      runMiner(rc);   break;
                    case SOLDIER:    runSoldier(rc); break;
                    case LABORATORY: break;
                    case WATCHTOWER: runWatchtower(rc); break;
                    case BUILDER:    runBuilder(rc); break;
                    case SAGE:       break;
                }
                randomMove(rc);
            }
            catch (GameActionException e) 
            {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } 
            catch (Exception e) 
            {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } 
            finally 
            {
                Clock.yield();
            }
        }
    }
    static void runArchon(RobotController rc) throws GameActionException 
    {
        MapLocation me = rc.getLocation();
        int roundNum = rc.getRoundNum();
        if (rc.getRoundNum() < 1) {
            MapLocation[] miningLocations = rc.senseNearbyLocationsWithLead(34);
            minerCount = 0;
            if (miningLocations.length > 0) {}
                Direction dir = rc.getLocation().directionTo(miningLocations[0]);
                if (rc.canBuildRobot(RobotType.MINER, dir)) {
                    rc.buildRobot(RobotType.MINER, dir);
                    minerCount++;
                }
        }
        Direction dir = directions[rng.nextInt(directions.length)];
        
        int archonID = ((int)Math.floor(Double.valueOf(rc.getID() / 2)) % 4);
        if (rc.readSharedArray(archonID) < 10000) {
            int xCoord = rc.getMapWidth() - me.x;
            int yCoord = rc.getMapHeight() - me.y;
            int toWrite = ((xCoord * 100) + yCoord);
            rc.writeSharedArray(archonID, toWrite);
        }
        
        if (minerCount < 20)
        {
            if (rc.canBuildRobot(RobotType.MINER, dir))
            {
                rc.buildRobot(RobotType.MINER, dir);
                minerCount++;
            }
        } 
        else 
        {
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) 
            {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
        }
        if ((rc.getMode() == RobotMode.TURRET) && rc.canTransform()) {
            rc.transform();
        }
        int xCoordTotalModified = 0;
        int yCoordTotalModified = 0;
        if ((roundNum < 5) || ((roundNum % 10) == 0)) {
            int toWrite = (((me.x * 100) + me.y) + 10000);
            rc.writeSharedArray((archonID + 4), toWrite);
        }
        if ((((rc.getMapWidth() - me.x) < 10) || (me.x < 10)) && (((rc.getMapHeight() - me.y) < 10) || (me.y < 10))) {
            RobotInfo[] nearbyRobots = rc.senseNearbyRobots(-1, rc.getTeam());
            //INSERT CODE to keep distance from allied archons to not clump too hard (blocks spawning spaces)

        } else {
            int archonLocation;
            Direction[] archonQuadrants = {Direction.CENTER, Direction.CENTER, Direction.CENTER, Direction.CENTER};

            for (int i = 0; i < 4; i++) {
                archonLocation = rc.readSharedArray(i + 4);
                if (archonLocation > 9999) {
                    int xCoordModified = ((int)Math.floor((archonLocation - 10000) / 100)) - (int)Math.floor(rc.getMapWidth() / 2);
                    int yCoordModified = (archonLocation - 10000) - (xCoordModified * 100) - (int)Math.floor(rc.getMapHeight() / 2);
                    xCoordTotalModified = xCoordTotalModified + xCoordModified;
                    yCoordTotalModified = yCoordTotalModified + yCoordModified;
                }
            }
            int xCoord = (xCoordTotalModified / Math.abs(xCoordTotalModified)) * ((int)Math.floor(rc.getMapWidth() / 2) - 5);
            int yCoord = (yCoordTotalModified / Math.abs(yCoordTotalModified)) * ((int)Math.floor(rc.getMapHeight() / 2) - 5);
            MapLocation targetToPathTo = new MapLocation(xCoord, yCoord);
            Direction directionToPathTo = rc.getLocation().directionTo(targetToPathTo);
            if (rc.canMove(directionToPathTo)) {
                rc.move(directionToPathTo);
            }
        }
    }
    static void runMiner(RobotController rc) throws GameActionException 
    {
        MapLocation me = rc.getLocation();
        int mhp=rc.getHealth();
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        int visiontile = -1;
        int height=rc.getMapHeight();
        Direction dir = directions[rng.nextInt(directions.length)];
        int width=rc.getMapWidth();
        int i = 1;
        if (i == 1) 
        { 
            MapLocation archonProducedMeLocation = rc.getLocation();
            i++;
        }
        if (mhp<=0)
        {
            minernumber--;
        }
        for (int dx = -1; dx <= 1; dx++) 
        {
            for (int dy = -1; dy <= 1; dy++) 
            {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while (rc.canMineGold(mineLocation)) 
                {
                    rc.mineGold(mineLocation);
                }
            }
        } 
        for (int dx = -1; dx <= 1; dx++) 
        {
            for (int dy = -1; dy <= 1; dy++) 
            {
                MapLocation mineLocation = new MapLocation(me.x + dx, me.y + dy);
                while ((rc.canMineLead(mineLocation)) && (rc.senseLead(mineLocation) >= 10)) {
                    rc.mineLead(mineLocation);
                }
            }
        }
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                MapLocation visionLocation =  new MapLocation(me.x + dx, me.y + dy);
                Direction mind = rc.getLocation().directionTo(visionLocation);
                if (rc.canSenseLocation(visionLocation)) {
                    visiontile++;
                    rubbleVision = rc.senseRubble(visionLocation);
                    leadVision = rc.senseLead(visionLocation);
                    goldVision = rc.senseGold(visionLocation);
                    if (goldVision!=0) {
                        if (rc.canMove(mind)) 
                        {
                            rc.move(mind);
                        }
                    } else if (leadVision!=0) {
                        if (rc.canMove(mind)) 
                        {
                            rc.move(mind);
                        }
                    } else if (rc.canSenseRobotAtLocation(visionLocation)) {
                        if (enemies.length > 0) {
                            MapLocation toleave = enemies[0].location;
                            Direction canleave = rc.getLocation().directionTo(toleave).opposite();
                            if (rc.canMove(canleave)) 
                            {
                                rc.move(canleave);
                            }
                        }
                    }
                }
            }
        }
        if (rc.getMovementCooldownTurns() == 0) {
            //  making a grid
            int fiveWidth = (int)Math.floor(Double.valueOf(rc.getMapWidth() / 5));
            int fiveHeight = (int)Math.floor(Double.valueOf(rc.getMapHeight() / 5));
            int rID = rc.getID();
            int xCoord = rID % fiveWidth;
            int yCoord = (int)Math.floor(Double.valueOf(rID / fiveWidth));
            MapLocation targetToPathTo = new MapLocation(xCoord, yCoord);
            // moving towards assigned point on grid until it finds a lead deposit
            Direction toGridPoint = rc.getLocation().directionTo(targetToPathTo);
            if (rc.canMove(toGridPoint)) {
                rc.move(toGridPoint);
            }
        }
        /**if (turnCount!= 1)
        {
            MapLocation earchon =  new MapLocation((width - archonloc2.x), (height - archonloc2.y));
            Direction enemyarchon = rc.getLocation().directionTo(earchon);
            if (rc.canMove(enemyarchon)) 
            {
                rc.move(enemyarchon);
            }
        }**/
    }
    static void runSoldier(RobotController rc) throws GameActionException 
    {
        MapLocation me = rc.getLocation();
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        int height=rc.getMapHeight();
        Direction dir = directions[rng.nextInt(directions.length)];
        int width=rc.getMapWidth(); // make it always accessible from everywhere
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        boolean thereIsArchonInVision = false;
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].type == RobotType.ARCHON) {
                MapLocation toAttack = enemies[i].location;
                thereIsArchonInVision = true;
                if (rc.canAttack(toAttack)) {
                    rc.attack(toAttack);
                }
            }
            int archonID = ((int)Math.floor(Double.valueOf(enemies[i].ID / 2)) % 4);
            MapLocation archonLocInVision = enemies[i].location;
            int toWrite = ((archonLocInVision.x * 100) + archonLocInVision.y);
            rc.writeSharedArray(archonID, toWrite);
        }
        if (enemies.length > 0) 
        {
            MapLocation toAttack = enemies[0].location;
            if (rc.canAttack(toAttack)) 
            {
                rc.attack(toAttack);
            }
        }
        // getCurrentAttackLocation
        int arrayToReadFrom = (rc.getID() % 4);
        int attackArrayInformation = rc.readSharedArray(arrayToReadFrom);
        if (attackArrayInformation < 10000) {
            int xCoord = (int)Math.floor(attackArrayInformation / 100);
            int yCoord = attackArrayInformation - (xCoord * 100);
            MapLocation attackLocation = new MapLocation(xCoord, yCoord);
            Direction attackDirection = rc.getLocation().directionTo(attackLocation);
            if (rc.canMove(attackDirection)) {
                rc.move(attackDirection);
            }
            if ((me == attackLocation) && (thereIsArchonInVision == false)) {
                rc.writeSharedArray(arrayToReadFrom, 10000);
            }
        } else {
            Direction randdirection = directions[rng.nextInt(directions.length)];
            if (rc.canMove(randdirection)) {
                rc.move(randdirection);
            }
        }
        

        /**if (turnCount!=1)
        {
            if (archonloc0!=null)
            {
                MapLocation earchon0 =  new MapLocation((width - archonloc0.x), (height - archonloc0.y));
                Direction enemyarchon0 = rc.getLocation().directionTo(earchon0);
                if (rc.canMove(enemyarchon0)) 
                {
                    rc.move(enemyarchon0);
                }
            }
            if (archonloc1!=null)
            {
                MapLocation earchon1 =  new MapLocation((width - archonloc1.x), (height - archonloc1.y));
                Direction enemyarchon1 = rc.getLocation().directionTo(earchon1);
                if (rc.canMove(enemyarchon1)) 
                {
                    rc.move(enemyarchon1);
                }
            }
            if (archonloc2!=null)
            {
                MapLocation earchon2 =  new MapLocation((width - archonloc2.x), (height - archonloc2.y));
                Direction enemyarchon2 = rc.getLocation().directionTo(earchon2);
                if (rc.canMove(enemyarchon2)) 
                {
                    rc.move(enemyarchon2);
                }
            }
            if (archonloc3!=null)
            {
                MapLocation earchon3 =  new MapLocation((width - archonloc3.x), (height - archonloc3.y));
                Direction enemyarchon3 = rc.getLocation().directionTo(earchon3);
                if (rc.canMove(enemyarchon3)) 
                {
                    rc.move(enemyarchon3);
                }
            }
            if (archonloc4!=null)
            {
                MapLocation earchon4 =  new MapLocation((width - archonloc4.x), (height - archonloc4.y));
                Direction enemyarchon4 = rc.getLocation().directionTo(earchon4);
                if (rc.canMove(enemyarchon4)) 
                {
                    rc.move(enemyarchon4);
                }
            }
            if (archonloc5!=null)
            {
                MapLocation earchon5 =  new MapLocation((width - archonloc5.x), (height - archonloc5.y));
                Direction enemyarchon5 = rc.getLocation().directionTo(earchon5);
                if (rc.canMove(enemyarchon5)) 
                {
                    rc.move(enemyarchon5);
                }
            }
            if (archonloc6!=null)
            {
                MapLocation earchon6 =  new MapLocation((width - archonloc6.x), (height - archonloc6.y));
                Direction enemyarchon6 = rc.getLocation().directionTo(earchon6);
                if (rc.canMove(enemyarchon6)) 
                {
                    rc.move(enemyarchon6);
                }
            }
            if (archonloc7!=null)
            {
                MapLocation earchon7 =  new MapLocation((width - archonloc7.x), (height - archonloc7.y));
                Direction enemyarchon7 = rc.getLocation().directionTo(earchon7);
                if (rc.canMove(enemyarchon7)) 
                {
                    rc.move(enemyarchon7);
                }
            }
            if (archonloc8!=null)
            {
                MapLocation earchon8 =  new MapLocation((width - archonloc8.x), (height - archonloc8.y));
                Direction enemyarchon8 = rc.getLocation().directionTo(earchon8);
                if (rc.canMove(enemyarchon8)) 
                {
                    rc.move(enemyarchon8);
                }
            }
            if (archonloc9!=null)
            {
                MapLocation earchon9 =  new MapLocation((width - archonloc9.x), (height - archonloc9.y));
                Direction enemyarchon9 = rc.getLocation().directionTo(earchon9);
                if (rc.canMove(enemyarchon9)) 
                {
                    rc.move(enemyarchon9);
                }
            }
        } **/
    }
    static void runWatchtower(RobotController rc) throws GameActionException {
        Team ally = rc.getTeam();
        Team opponent = Team.A;
        if (ally == Team.A) {
            opponent = Team.B;
        }
        MapLocation me = rc.getLocation();
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots(me, -1, opponent);
        int[] robotvalue = new int[nearbyRobots.length]; // makes targeting value array for each enemy robot
        int rhealth;
        RobotType rtype;
        for (int i = 0; i < nearbyRobots.length; i++) {
            rtype = nearbyRobots[i].type; // gets type of robot

            rhealth = nearbyRobots[i].health; // gets health of robot

            robotvalue[i] = (100 - rhealth); // creating priority valuations
            // going for last hits with priority on higher value units
            if (rtype == RobotType.SAGE) {
                robotvalue[i] = robotvalue[i] + 200;
            }
            if (rtype == RobotType.WATCHTOWER) {
                robotvalue[i] = robotvalue[i] + 150;
            }
            if (rtype == RobotType.ARCHON) {
                robotvalue[i] = robotvalue[i] + 1500;
            }
            if (rtype == RobotType.SOLDIER) {
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
        MapLocation rlocation = nearbyRobots[index].location;
        if (rc.canAttack(rlocation)) {
            rc.attack(rlocation);
        }
    }

    static void runBuilder(RobotController rc) throws GameActionException {
        Team ally = rc.getTeam();
        Team opponent = Team.A;
        if (ally == Team.A) {
            opponent = Team.B;
        }
        MapLocation me = rc.getLocation();

            // three possible statuses - need to decide how to set it
            // 0: target repair status, lock onto a normal building and repair it over and over, apply repair to secondary target if primary target is full hp
            // 1: laboratory build/repair status, use recommended coordinates saved and provided (probably recorded) to build specific building
            // 2: same as above but for turrets

        MapLocation currentTargetLocation = me; //goal lab location, goal watchtower prototype location or defending a target
        // INSERT FUNCTION/COMMUNICATION TO DETERMINE THE ABOVE VARIABLES
        int builderStatus = 0; //remove forced initial value once above is in place
        if (builderStatus == 0){
            // first try to repair target, if it doesn't need healing, then search for other viable targets
            RobotInfo[] nearbyRobots = rc.senseNearbyRobots(me, -1, ally);
            int rhealth;
            RobotType rtype;
            int[] robotvalue = new int[nearbyRobots.length];
            int turns = rc.getActionCooldownTurns();
            if ((rc.canRepair(currentTargetLocation)) && (turns == 0)) {
                RobotInfo currentTargetRobot = rc.senseRobotAtLocation(currentTargetLocation);
                rtype = currentTargetRobot.type;
                rhealth = currentTargetRobot.health;
                if ((rtype == RobotType.WATCHTOWER) && (rhealth < 110)) {
                    rc.repair(currentTargetLocation);
                } else if ((rtype == RobotType.ARCHON) && (rhealth < 950)) {
                    rc.repair(currentTargetLocation);
                } else if ((rtype == RobotType.LABORATORY) && (rhealth < 80)) {
                    rc.repair(currentTargetLocation);
                }
            } else if (turns == 0) {
                for (int i = 0; i < nearbyRobots.length; i++) {
                    rtype = nearbyRobots[i].type; // gets type of robot
                    rhealth = nearbyRobots[i].health; // gets health of robot
                    if ((rtype == RobotType.WATCHTOWER)) {
                        robotvalue[i] = 100 - rhealth;
                    } else if ((rtype == RobotType.ARCHON)) {
                        robotvalue[i] = 1300 - rhealth;
                    } else if ((rtype == RobotType.LABORATORY)) {
                        robotvalue[i] = 10 - rhealth;                   }
                }
                int maximum = robotvalue[0];   // find maximum robotvalue and index
                int index = 0;
                for (int i=1; i<robotvalue.length; i++) {
                    if (robotvalue[i] > maximum) {
                        maximum = robotvalue[i];
                        index = i;
                    }
                }
                MapLocation rlocation = nearbyRobots[index].location;
                if (rc.canRepair(rlocation)) {
                    rc.repair(rlocation);
                }
            }
        } else if (((builderStatus == 1) || (builderStatus == 2)) && (rc.canSenseLocation(currentTargetLocation))) {
            // if builder is actively pathfinding to currentTargetLocation, this part of the code can be ignored
            // if builder is within range of currentTargetLocation, check what is there
                // if there is a completed full hp building, switch to mode 0 with the same target location until overridden
                // if there is a partial hp prototype, repair it
                // if there is nothing there, place a prototype
            RobotInfo targetRobot = rc.senseRobotAtLocation(currentTargetLocation);
            Team rteam = targetRobot.team;
            RobotMode rmode = targetRobot.mode;
            RobotType rTargetType = RobotType.ARCHON; // will do nothing if below isn't chosen
            if (builderStatus == 1) {
                rTargetType = RobotType.LABORATORY;
            } else if (builderStatus == 2) {
                rTargetType = RobotType.WATCHTOWER;
            }
            if (rteam == ally) {
                if (rmode == RobotMode.PROTOTYPE) {
                    if (rc.canRepair(currentTargetLocation)) {
                        rc.repair(currentTargetLocation);
                    }
                } else if (rmode == RobotMode.TURRET) {
                    builderStatus = 0;
                    if (rc.canRepair(currentTargetLocation)) {
                        rc.repair(currentTargetLocation);
                    }
                }
            } else if (!(rc.canSenseRobotAtLocation(currentTargetLocation))) {
                Direction targetDirection;
                for (int i = 0; i < directions.length; i++) {
                    if (rc.adjacentLocation(directions[i]) == currentTargetLocation) {
                        if (rc.canBuildRobot(rTargetType, directions[i])) {
                            rc.buildRobot(rTargetType, directions[i]);
                        }
                    }
                }
            } else {
                for (int i = 0; i < directions.length; i++) {
                    if (rc.canBuildRobot(rTargetType, directions[i])) {
                        rc.buildRobot(rTargetType, directions[i]);
                    }
                }
            }
        }
    }
    static void randomMove(RobotController rc) throws GameActionException {
            Direction randdirection = directions[rng.nextInt(directions.length)];
            if (rc.canMove(randdirection)) {
                rc.move(randdirection);
            }
    }
}  
