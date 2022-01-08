package original_taohao;
import battlecode.common.*;

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
    /**
    static MapLocation archonloc0 = null;
    static MapLocation archonloc1 = null;
    static MapLocation archonloc2 = null;
    static MapLocation archonloc3 = null;
    static MapLocation archonloc4 = null;
    static MapLocation archonloc5 = null;
    static MapLocation archonloc6 = null;
    static MapLocation archonloc7 = null;
    static MapLocation archonloc8 = null;
    static MapLocation archonloc9 = null;
     */
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
        System.out.println("I'm a " + rc.getType() + " and I just got created! I have health " + rc.getHealth());
        rc.setIndicatorString("Hello world!");
        while (true) 
        {
            turnCount += 1;
            System.out.println("Age: " + turnCount + "; Location: " + rc.getLocation());
            try 
            {
                switch (rc.getType()) 
                {
                    case ARCHON:     runArchon(rc);  break;
                    case MINER:      runMiner(rc);   break;
                    case SOLDIER:    runSoldier(rc); break;
                    case LABORATORY: 
                    case WATCHTOWER: 
                    case BUILDER:
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
        Direction dir = directions[rng.nextInt(directions.length)];
        int archoncode = rc.getID();
        if (archoncode==0);
        {
            archonloc0 = rc.getLocation();
        }
        if (archoncode==1);
        {
            archonloc1 = rc.getLocation();
        }
        if (archoncode==2);
        {
            archonloc2 = rc.getLocation();
        }
        if (archoncode==3);
        {
            archonloc3 = rc.getLocation();
        }
        if (archoncode==4);
        {
            archonloc4 = rc.getLocation();
        }
        if (archoncode==5);
        {
            archonloc5 = rc.getLocation();
        }
        if (archoncode==6);
        {
            archonloc6 = rc.getLocation();
        }
        if (archoncode==7);
        {
            archonloc7 = rc.getLocation();
        }
        if (archoncode==8);
        {
            archonloc8 = rc.getLocation();
        }
        if (archoncode==9);
        {
            archonloc9 = rc.getLocation();
        }
        if (turnCount!=0)//turnCount%2==0) 
        {
            if (rc.canBuildRobot(RobotType.MINER, dir)) 
            {
                rc.buildRobot(RobotType.MINER, dir);
            }
            else
            {
                turnCount--;
            }
        } 
        else 
        {
            rc.setIndicatorString("Trying to build a soldier");
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) 
            {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
            else
            {
                turnCount--;
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
                while (rc.canMineLead(mineLocation)) 
                {
                    rc.mineLead(mineLocation);
                }
            }
        }
        for (int dx = -3; dx <= 3; dx++) 
        {
            for (int dy = -3; dy <= 3; dy++) 
            {
                MapLocation visionLocation =  new MapLocation(me.x + dx, me.y + dy);
                Direction mind = rc.getLocation().directionTo(visionLocation);
                if (rc.canSenseLocation(visionLocation)) 
                {
                    visiontile++;
                    rubbleVision = rc.senseRubble(visionLocation);
                    leadVision = rc.senseLead(visionLocation);
                    goldVision = rc.senseGold(visionLocation);
                    if (leadVision!=0)
                    {
                        if (rc.canMove(mind)) 
                        {
                            rc.move(mind);
                        }
                    }
                    if (goldVision!=0)
                    {
                        if (rc.canMove(mind)) 
                        {
                            rc.move(mind);
                        }
                    }
                    if (rc.canSenseRobotAtLocation(visionLocation)) 
                    {
                        if (enemies.length > 0) 
                        {
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
        if (turnCount!=1)
        {
            MapLocation earchon =  new MapLocation((width - archonloc2.x), (height - archonloc2.y));
            Direction enemyarchon = rc.getLocation().directionTo(earchon);
            if (rc.canMove(enemyarchon)) 
            {
                rc.move(enemyarchon);
            }
        }
    }
    static void runSoldier(RobotController rc) throws GameActionException 
    {
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        int height=rc.getMapHeight();
        Direction dir = directions[rng.nextInt(directions.length)];
        int width=rc.getMapWidth(); // make it always accessible from everywhere
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) 
        {
            MapLocation toAttack = enemies[0].location;
            if (rc.canAttack(toAttack)) 
            {
                rc.attack(toAttack);
            }
        }
        if (turnCount!=1)
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
        }
    }
}
