import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Zombie extends Animateable {

    public Zombie(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strat = new AStarPathingStrategy();
        Predicate<Point> canPass = (Point p) -> (!world.isOccupied(p) && world.withinBounds(p));
        BiPredicate<Point, Point> withinReach = (Point p1, Point p2) -> p1.adjacent(p2);
        ArrayList<Point> path = (ArrayList<Point>) strat.computePath(this.getPosition(), destPos, p -> world.withinBounds(p) && !world.isOccupied(p), withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        Point newPos;
        if (path.size() > 0) newPos = path.get(0);
        else newPos = this.getPosition();
        return newPos;
    }

    public  boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition())) {
            return true;
        }
        else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }
    @Override
    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Entity actualTarget = null;
        Optional<Entity> fullTarget =
                world.findNearest(this.getPosition(), new MinerFull(null, null, null, 0, 0, 0, 0));
        Optional<Entity> notFullTarget =
                world.findNearest(this.getPosition(), new MinerNotFull(null, null, null, 0, 0, 0, 0));
        if (fullTarget.isPresent() && notFullTarget.isPresent()) {
            if (this.getPosition().dist(fullTarget.get().getPosition()) < this.getPosition().dist(notFullTarget.get().getPosition())) {
                actualTarget = fullTarget.get();
            } else {
                actualTarget = notFullTarget.get();
            }
        } else if (fullTarget.isPresent()) {
            actualTarget = fullTarget.get();
        } else if (notFullTarget.isPresent()) {
            actualTarget = notFullTarget.get();
        }

        if (actualTarget == null) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        } else {
            if (!(this.moveTo(world, actualTarget, scheduler))) {
                scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
            } else {
                ((Miner)actualTarget).makeZombie(world, scheduler, imageStore);
                scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());


            }
        }


    }
    public void makeMiner(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Point getPos = new Point(this.getPosition().x, this.getPosition().y);
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        MinerNotFull miner = Factory.createMinerNotFull(this.getId(), 4,
                getPos, 813,
                100,
                imageStore.images.get("miner"));
        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }
}
