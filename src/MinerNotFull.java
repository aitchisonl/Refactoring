import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerNotFull extends Miner{

    public MinerNotFull(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod)
    {
       super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }

    @Override
    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget =
                world.findNearest(this.getPosition(), new Ore(null, null, null, 0));


        if (!notFullTarget.isPresent() || !moveTo( world,
                notFullTarget.get(),
                scheduler)
                || !this.transform(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.getActionPeriod());
        }
    }


    public boolean transform(WorldModel world,
                                    EventScheduler scheduler,
                                    ImageStore imageStore)
    {
        if (this.getResourceCount() >= this.getResourceLimit()) {
            MinerFull miner = Factory.createMinerFull(this.getId(), this.getResourceLimit(),
                    this.getPosition(), this.getActionPeriod(),
                    this.getAnimationPeriod(),
                    this.getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }




    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition())) {
            this.setResourceCount(this.getResourceCount()+1);
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else {
            Point nextPos = this.nextPosition(world, target.getPosition());
            //Point nextPos = this.nextPosition(world, world.getMousePos());
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
}
