public class Animation implements Action{
    public Animateable entity;
    public int repeatCount;
    public Animation(
            Animateable entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public  void executeAction(EventScheduler scheduler)
    { this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent((Entity) this.entity,
                    this.entity.createAnimationAction(
                            Math.max(this.repeatCount - 1,
                                    0)),
                    this.entity.getAnimationPeriod());
        }
    }
}
