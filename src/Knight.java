import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Knight extends Animateable {

    public Knight(
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
            EventScheduler scheduler)
    {
        Optional<Entity> actualTarget = world.findNearest(this.getPosition(), new Zombie(null, null, null, 0, 0));
        if (!actualTarget.isPresent()) {
            actualTarget = world.findNearest(this.getPosition(), new OreBlob(null, null, null, 0, 0));

        }

        if (!actualTarget.isPresent()) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        } else {
            if (!(this.moveTo(world, actualTarget.get(), scheduler))) {
                scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
            } else {
                if (actualTarget.get() instanceof Zombie) {
                    ((Zombie) actualTarget.get()).makeMiner(world, scheduler, imageStore);
                } else {
                    world.removeEntity(actualTarget.get());
                }
                scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());


            }
        }

    }
    public void makeZombie(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Point getPos = new Point(this.getPosition().x, this.getPosition().y);
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
        Zombie newZombie = Factory.createZombie("6", getPos, 600, 10, imageStore.images.get("zombie"));
        world.addEntity(newZombie);
        newZombie.scheduleActions(scheduler, world, imageStore);
    }
}
