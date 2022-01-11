package taohao4;
import battlecode.common.*;
// import javafx.beans.binding.MapBinding; // won't compile on submission
import java.lang.Math;
import java.security.cert.TrustAnchor;
import java.util.Random;
public strictfp class RobotPlayer {
    static int turnCount = 0;
    static final Random rng = new Random(6147);
    static int minernumber=0;
    static int rubbleVision = 0;
    static int leadVision = 0;
    static int robotAge=1;
    static int robotAge2=1;
    static int goldVision = 0;
    static int onlead=0;
    static int minerCount = 0;
    static int builderStatus = 0;
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
    static int x=1;
    static MapLocation currentTargetLocation; // for builders

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
        int myID = (int)rc.getID();
        int turn = rc.getRoundNum();
        if (rc.getRoundNum() == 1)
        {
            MapLocation[] miningLocations = rc.senseNearbyLocationsWithLead(34);
            if (miningLocations.length > 0) 
            {
                minerCount = miningLocations.length;
                Direction dir = rc.getLocation().directionTo(miningLocations[0]);
                if ((rc.canBuildRobot(RobotType.MINER, dir)) && (minerCount > 0))
                {
                    rc.buildRobot(RobotType.MINER, dir);
                    minerCount--;
                }
            }
        }
        Direction dir = directions[rng.nextInt(directions.length)];

        if (rc.getRoundNum() == 1) {
            int archonID = ((int)Math.floor(Double.valueOf((rc.getID() - 2) / 2)) % 4);
            if (rc.readSharedArray(archonID) < 10000) {
                int xCoord = rc.getMapWidth() - me.x;
                int yCoord = rc.getMapHeight() - me.y;
                int toWrite = ((xCoord * 100) + yCoord);
                rc.writeSharedArray(archonID, toWrite);
            }
        }
        if (rc.getRoundNum() > 1) {
            int archonID = (((int)Math.floor(Double.valueOf((rc.getID() - 2) / 2)) % 4) + 4);
            int xCoord = me.x;
            int yCoord = me.y;
            int toWrite = ((xCoord * 100) + yCoord);
            rc.writeSharedArray(archonID, toWrite);
        }
        if  (((rc.getRoundNum() % 100) == 0) && (myID < 4)) {
            if (rc.canBuildRobot(RobotType.BUILDER, dir))
                {
                    rc.buildRobot(RobotType.BUILDER, dir);
                }
        }
        
        //build other robots if possible
        if (!(((turn % 100) < 100) && ((turn % 100) > 70)) && (turn > 20)) {
            if ((turn % 10) == 1)
            {
                if (rc.canBuildRobot(RobotType.MINER, dir))
                {
                    rc.buildRobot(RobotType.MINER, dir);
                    minerCount--;
                }
            }
            else if ((turn % 10) > 2)
            {
                if (rc.canBuildRobot(RobotType.SOLDIER, dir)) 
                {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                }
            }
        }

        // define main archon (the safest positioned one, and move any other archons towards it to fortify)
        
        if (myID > 3 && rc.getArchonCount() > 2) {
            if ((rc.getRoundNum() > 20) && (rc.getRoundNum() < 25)) {
                if ((rc.getMode() == RobotMode.TURRET) && (rc.canTransform())) {
                    rc.transform();
                }
            }
            int mainArchon = rc.readSharedArray(4);
            int xCoord = (int)Math.floor(mainArchon / 100);
            int yCoord = mainArchon - (xCoord * 100);
            MapLocation mainArchonLoc = new MapLocation(xCoord, yCoord);
            if (rc.getLocation().distanceSquaredTo(mainArchonLoc) > 15) {
                Direction movementDirection = rc.getLocation().directionTo(mainArchonLoc);
                if (rc.canMove(movementDirection)) {
                    rc.move(movementDirection);
                }
            } else if ((rc.getLocation().distanceSquaredTo(mainArchonLoc) <= 15) && (rc.getMode() == RobotMode.PORTABLE)){
                if (rc.canTransform()) {
                    rc.transform();
                }
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
        boolean thereIsArchonInVision = false;
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].type == RobotType.ARCHON) {
                MapLocation toAttack = enemies[i].location;
                thereIsArchonInVision = true;
                if (rc.canAttack(toAttack)) 
                {
                }
            }
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
                while ((rc.canMineLead(mineLocation)) && (rc.senseLead(mineLocation) >= 3)) {
                    rc.mineLead(mineLocation);
                }
            }
        }
        boolean doINeedToMove = true;
        if (rc.senseLead(me) >= 1 || rc.senseGold(me) >= 1) {
            doINeedToMove = false;
        }
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                MapLocation visionLocation =  new MapLocation(me.x + dx, me.y + dy);
                Direction mind = rc.getLocation().directionTo(visionLocation);
                if (rc.canSenseLocation(visionLocation)) {
                    visiontile++;
                    onlead = rc.senseLead(me);
                    rubbleVision = rc.senseRubble(visionLocation);
                    leadVision = rc.senseLead(visionLocation);
                    goldVision = rc.senseGold(visionLocation);
                    if (!rc.canSenseRobotAtLocation(visionLocation)) {
                        if (goldVision!=0) {
                            if (rc.canMove(mind) && doINeedToMove) 
                            {
                                rc.move(mind);
                            }
                        } 
                        else if ((leadVision!=0) && doINeedToMove)
                        {   if (onlead==0)
                            {
                                if (rc.canMove(mind)) 
                                {
                                    rc.move(mind);
                                }
                            }
                        }
                    }
                    else if (rc.canSenseRobotAtLocation(visionLocation)) {
                        if (enemies.length > 0) {
                            MapLocation toleave = enemies[0].location;
                            Direction canleave = rc.getLocation().directionTo(toleave).opposite();
                            if (rc.canMove(canleave) && doINeedToMove) 
                            {
                                rc.move(canleave);
                            }
                        }
                    }
                }
            }
        }
        int arrayToReadFrom = (rc.getID() % 4);
        int attackArrayInformation = rc.readSharedArray(arrayToReadFrom);
        if (attackArrayInformation < 10000) {
            int xCoord = (int)Math.floor(attackArrayInformation / 100);
            int yCoord = attackArrayInformation - (xCoord * 100);
            MapLocation attackLocation = new MapLocation(xCoord, yCoord);
            Direction attackDirection = rc.getLocation().directionTo(attackLocation);
            if (rc.canMove(attackDirection) && doINeedToMove) {
                rc.move(attackDirection);
            }
            if ((me == attackLocation) && (thereIsArchonInVision == false)) {
                rc.writeSharedArray(arrayToReadFrom, 10000);
            }
        } else {
            Direction randdirection = directions[rng.nextInt(directions.length)];
            if (rc.canMove(randdirection) && doINeedToMove) {
                rc.move(randdirection);
            }
        }
        if (rc.getMovementCooldownTurns() == 0) 
        {
            //  making a grid
            int fiveWidth = (int)Math.floor(Double.valueOf(rc.getMapWidth() / 5));
            int fiveHeight = (int)Math.floor(Double.valueOf(rc.getMapHeight() / 5));
            int rID = rc.getID();
            int xCoord = rID % fiveWidth;
            int yCoord = (int)Math.floor(Double.valueOf(rID / fiveWidth));
            MapLocation targetToPathTo = new MapLocation(xCoord, yCoord);
            // moving towards assigned point on grid until it finds a lead deposit
            Direction toGridPoint = rc.getLocation().directionTo(targetToPathTo);
            if (rc.canMove(toGridPoint) && doINeedToMove) 
            {
                rc.move(toGridPoint);
            }
            if (rc.isActionReady()) {
                randomMove(rc);
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
        if (rc.getRoundNum() <= 1300) {
            int mainArchon = rc.readSharedArray(4);
            int xaCoord = (int)Math.floor(mainArchon / 100);
            int yaCoord = mainArchon - (xaCoord * 100);
            MapLocation mainArchonLoc = new MapLocation(xaCoord, yaCoord);
            int secondArchon = rc.readSharedArray(5);
            xaCoord = (int)Math.floor(secondArchon / 100);
            yaCoord = secondArchon - (xaCoord * 100);
            MapLocation secondArchonLoc = new MapLocation(xaCoord, yaCoord);
            if (rc.getLocation().distanceSquaredTo(mainArchonLoc) < rc.getLocation().distanceSquaredTo(secondArchonLoc)) {
                if (rc.getLocation().distanceSquaredTo(mainArchonLoc) < (Math.pow(((rc.getMapHeight() + rc.getMapWidth()) / 4), 2))) {
                    Direction movementDirection = rc.getLocation().directionTo(mainArchonLoc);
                    if (rc.canMove(movementDirection)) {
                        rc.move(movementDirection);
                    }
                }
            } else if (rc.getLocation().distanceSquaredTo(mainArchonLoc) > rc.getLocation().distanceSquaredTo(secondArchonLoc)) {
                if (rc.getLocation().distanceSquaredTo(mainArchonLoc) < (Math.pow(((rc.getMapHeight() + rc.getMapWidth()) / 3.3), 2))) {
                    Direction movementDirection = rc.getLocation().directionTo(mainArchonLoc);
                    if (rc.canMove(movementDirection)) {
                        rc.move(movementDirection);
                    }
                }
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
        if (rc.getActionCooldownTurns() == 0) {
            randomMove(rc);
        }
    }
    static void runWatchtower(RobotController rc) throws GameActionException {
        Team ally = rc.getTeam();
        Team opponent = Team.A;
        if (ally == Team.A) {
            opponent = Team.B;
        }
        if (rc.getMode() == RobotMode.PORTABLE) {
            if (rc.canTransform()) {
                rc.transform();
            }
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
                robotvalue[i] = robotvalue[i] + 10000;
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

    static void runBuilder(RobotController rc) throws GameActionException 
    {
        Team ally = rc.getTeam();
        Team opponent = Team.A;
        if (ally == Team.A) {
            opponent = Team.B;
        }
        int myID = (int)rc.getID();
        int turn = rc.getRoundNum();
        MapLocation me = rc.getLocation();
        RobotInfo[] robotdirection=rc.senseNearbyRobots();
        Direction randdirection = directions[rng.nextInt(directions.length)];
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots(me, -1, ally);
        int rhealth;
        MapLocation currentTargetLocation = me;
        RobotType rtype;
        RobotMode rmode;
        int[] robotvalue = new int[nearbyRobots.length];
        int turns = rc.getActionCooldownTurns();
        if ((rc.canRepair(currentTargetLocation)) && (turns == 0)) {
            RobotInfo currentTargetRobot = rc.senseRobotAtLocation(currentTargetLocation);
            rtype = currentTargetRobot.type;
            rhealth = currentTargetRobot.health;
            rmode = currentTargetRobot.mode;
            if (rmode == RobotMode.PROTOTYPE) {
                rc.repair(currentTargetLocation);
            } else if ((rtype == RobotType.WATCHTOWER) && (rhealth < 110)) {
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
                rmode = nearbyRobots[i].mode;
                if (rmode == RobotMode.PROTOTYPE) {
                    robotvalue[i] = 1100;
                } else if ((rtype == RobotType.WATCHTOWER)) {
                    robotvalue[i] = 100 - rhealth;
                    if (rhealth == 130) {
                        robotvalue[i] = 0;
                    }
                } else if ((rtype == RobotType.ARCHON)) {
                    robotvalue[i] = 1300 - rhealth;
                    if (rhealth == 1000) {
                        robotvalue[i] = 0;
                    }
                } else if ((rtype == RobotType.LABORATORY)) {
                    robotvalue[i] = 10 - rhealth;
                    if (rhealth == 100) {
                        robotvalue[i] = 0;
                    }
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
            if (rc.canRepair(rlocation)) {
                rc.repair(rlocation);
            }
        }
        if ((rc.getRoundNum() % 10) == 1) {
            if (!rc.canSenseRobotAtLocation(rc.adjacentLocation(randdirection))) {
                rc.buildRobot(RobotType.WATCHTOWER,randdirection);
            }
        }
        if (me.y < 5) {
            if (rc.canMove(Direction.NORTH)) {
                rc.move(Direction.NORTH);
            }
        } else if (rc.getMapHeight() - me.y < 5) {
            if (rc.canMove(Direction.SOUTH)) {
                rc.move(Direction.SOUTH);
            }
        } else if (me.x < 5) {
            if (rc.canMove(Direction.EAST)) {
                rc.move(Direction.EAST);
            }
        } else if (rc.getMapWidth() - me.x < 5) {
            if (rc.canMove(Direction.WEST)) {
                rc.move(Direction.WEST);
            }
        } else if (turns == 0) {
            randomMove(rc);
        }
    }


    /** static void runBuilder(RobotController rc) throws GameActionException {
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

        // INSERT FUNCTION/COMMUNICATION TO DETERMINE THE ABOVE VARIABLES
        if ((rc.getRoundNum() % 100) == 1) {
            MapLocation testLocation;
            for (int i = 0; i < directions.length; i++) {
                testLocation = rc.adjacentLocation(directions[i]);
                if (!(rc.canSenseRobotAtLocation(testLocation))) {
                    currentTargetLocation = testLocation;
                    builderStatus = 2;
                }
            }
        }


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
            RobotInfo targetRobot = rc.senseRobotAtLocation(me);
            if (rc.canSenseRobotAtLocation(currentTargetLocation)) {
                targetRobot = rc.senseRobotAtLocation(currentTargetLocation);
            }
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
    } **/
    static void randomMove(RobotController rc) throws GameActionException {
            Direction randdirection = directions[rng.nextInt(directions.length)];
            if (rc.canMove(randdirection)) {
                rc.move(randdirection);
            }
    }
}  
