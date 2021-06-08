public class Activity implements Action{
    public Actionable entity;
    public WorldModel world;
    public ImageStore imageStore;
    public Activity(
            Actionable entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }
    public void executeAction(EventScheduler scheduler)
    {
        this.entity.executeActivity(this.world,
                this.imageStore, scheduler);
    }
}
