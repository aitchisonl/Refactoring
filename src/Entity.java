import java.util.List;
import java.util.Optional;

import processing.core.PImage;

public abstract class Entity
{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }


    public PImage getCurrentImage() {
        return (this).images.get((this).imageIndex);
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point pos) {
        this.position = pos;
    }

    public String getId() {
        return this.id;
    }

    public List<PImage> getImages() {
        return images;
    }

    public int getImageIndex() {
        return this.imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }
}
