import processing.core.PImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
public abstract class Miner extends Animateable{
    private int resourceLimit;
    private int resourceCount;

    public Miner(String id,
                 Point position,
                 List<PImage> images,
                 int resourceLimit,
                 int resourceCount,
                 int actionPeriod,
                 int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    public void setResourceCount(int newCount) {
        this.resourceCount = newCount;
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos)
    {
        PathingStrategy strat = new AStarPathingStrategy();
        Predicate<Point> canPass = (Point p) -> (!world.isOccupied(p) && world.withinBounds(p));
        BiPredicate<Point, Point> withinReach = (Point p1, Point p2) -> p1.adjacent(p2);
        ArrayList<Point> path = (ArrayList<Point>) strat.computePath(this.getPosition(), destPos, p ->  world.withinBounds(p) && !world.isOccupied(p), withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        Point newPos;
        if(path.size() > 0) newPos = path.get(0); else newPos = this.getPosition();


        return newPos;
    }
    public void makeZombie(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Point getPos = new Point(this.getPosition().x, this.getPosition().y);
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        Zombie newZombie = Factory.createZombie("6", getPos, 600, 10, imageStore.images.get("zombie"));
        world.addEntity(newZombie);
        newZombie.scheduleActions(scheduler, world, imageStore);
    }
    public void makeKnight(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Point getPos = new Point(this.getPosition().x, this.getPosition().y);
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        Knight newZombie = Factory.createKnight("7", getPos, 500, 100, imageStore.images.get("knight"));
        world.addEntity(newZombie);
        newZombie.scheduleActions(scheduler, world, imageStore);
    }
}

