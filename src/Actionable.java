import processing.core.PImage;

import java.util.List;

public abstract class Actionable extends Entity{

    private int actionPeriod;

    public Actionable(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }
    public Action createActivityAction(WorldModel world, ImageStore imageStore)
    {
        return new Activity(this, world, imageStore, 0);
    }

    public abstract void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler);

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
    }
    public int getActionPeriod() {
        return this.actionPeriod;
    }
}
