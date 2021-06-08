import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class OreBlob extends Animateable{

    public OreBlob(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos)
    {

        PathingStrategy strat = new AStarPathingStrategy();
        Predicate<Point> canPass = (Point p) -> ((!world.isOccupied(p) || world.getOccupancyCell(p) instanceof Ore) && world.withinBounds(p));
        BiPredicate<Point, Point> withinReach = (Point p1, Point p2) -> p1.adjacent(p2);
        ArrayList<Point> path = (ArrayList<Point>) strat.computePath(this.getPosition(), destPos, p ->  world.withinBounds(p) && !world.isOccupied(p), withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        Point newPos;
        if(path.size() > 0) newPos = path.get(0); else newPos = this.getPosition();

        return newPos;
    }
    @Override
    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
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
    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> blobTarget =
                world.findNearest(this.getPosition(), new Vein(null, null, null, 0));
        System.out.println(blobTarget.isPresent());
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (this.moveTo(world, blobTarget.get(), scheduler)) {
                Quake quake = Factory.createQuake(tgtPos,
                        imageStore.getImageList(Functions.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                nextPeriod);
    }

}
