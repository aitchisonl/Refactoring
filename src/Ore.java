import processing.core.PImage;

import java.util.List;

public class Ore extends Actionable{

    public Ore(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod)
    {
        super(id, position, images, actionPeriod);
    }
    public  void executeActivity(

            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = this.getPosition();

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        OreBlob blob = Factory.createOreBlob(this.getId() + Functions.BLOB_ID_SUFFIX, pos,
                this.getActionPeriod() / Functions.BLOB_PERIOD_SCALE,
                Functions.BLOB_ANIMATION_MIN + Functions.rand.nextInt(
                        Functions.BLOB_ANIMATION_MAX
                                - Functions.BLOB_ANIMATION_MIN),
                imageStore.getImageList(Functions.BLOB_KEY));

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }


}
