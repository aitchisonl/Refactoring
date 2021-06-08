import processing.core.PImage;

import java.util.List;

public abstract class Animateable extends Actionable {

    private int animationPeriod;

    public Animateable(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
    }
    public Action createAnimationAction(int repeatCount) {
        return new Animation(this, null, null,
                repeatCount);
    }

    public int getAnimationPeriod() {
        return this.animationPeriod;
    }

    public  void nextImage() {
        this.setImageIndex((this.getImageIndex() + 1) % this.getImages().size());
    }

    public abstract Point nextPosition(WorldModel world, Point destPos);

    public abstract boolean moveTo(WorldModel world,
                                   Entity target,
                                   EventScheduler scheduler);
    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent(this,
                this.createAnimationAction(0),
                this.getAnimationPeriod());
    }

}
