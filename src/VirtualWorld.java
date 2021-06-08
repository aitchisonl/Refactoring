import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import processing.core.*;

public final class VirtualWorld extends PApplet
{
    public static final int TIMER_ACTION_PERIOD = 100;

    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int WORLD_WIDTH_SCALE = 2;
    public static final int WORLD_HEIGHT_SCALE = 2;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String LOAD_FILE_NAME = "world.sav";

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    public static double timeScale = 1.0;

    public ImageStore imageStore;
    public WorldModel world;
    public WorldView view;
    public EventScheduler scheduler;
    public int xOffset = 0;
    public int yOffset = 0;

    public long nextTime;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                                   DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                                    createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                                  TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }
        this.world.setMousePos(mouseToPoint(mouseX, mouseY));
        Point p = world.getMousePos();
        fill(255, 0, 0);
        view.drawViewport();
        rect(p.x * TILE_WIDTH + TILE_WIDTH * 3 / 8,
                p.y * TILE_WIDTH + TILE_WIDTH * 3 / 8,
                TILE_WIDTH / 4, TILE_WIDTH / 4);
    }

    public void mousePressed() {

        Point pressed = mouseToPoint(mouseX, mouseY);
        ArrayList<Entity> toBeChanged = new ArrayList<Entity>();
        for (Entity e : world.getEntities()) {
            if (e instanceof Miner || e instanceof Knight) {
                if (pressed.dist(e.getPosition()) < 3) toBeChanged.add(e);
            }
        }
        for (Entity e : toBeChanged) {
            if (e instanceof Miner)
            ((Miner) e).makeZombie(world, scheduler, imageStore);
            else ((Knight) e).makeZombie(world, scheduler, imageStore);

        }
        Zombie newZombie = Factory.createZombie("6", pressed, 600, 10, imageStore.images.get("zombie"));
        newZombie.scheduleActions(scheduler, world, imageStore);
        world.tryAddEntity(newZombie);
        ArrayList<Point> neighbors = new ArrayList<Point>();
        neighbors.add(pressed);
        for (Point p: PNeighbors(pressed)) {
            neighbors.add(p);
        }
        int startLen = neighbors.size();
        for (int i = 0; i < startLen; i++) {
            Point p = neighbors.get(i);
            for (Point p2: PNeighbors(p)) {
                if (!(neighbors.contains(p2))) neighbors.add(p2);
            }
        }
        Random random= new Random();
        for (int i = 0; i < 7; i++) {
            int changeIndex = random.nextInt(7);
            Point toChange = neighbors.get(changeIndex);
            neighbors.remove(changeIndex);
            world.setBackground(toChange, new Background(Functions.BGND_KEY, imageStore.images.get("deadgrass")));


        }
        redraw();
    }
    private List<Point> PNeighbors(Point p) {
        ArrayList<Point> returnList = new ArrayList<Point>();
        returnList.add(new Point(p.x+1, p.y));
        returnList.add(new Point(p.x, p.y+1));
        returnList.add(new Point(p.x, p.y-1));
        returnList.add(new Point(p.x-1, p.y));
        return returnList;
    }
    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    yOffset += dy;
                    break;
                case DOWN:
                    dy = 1;
                    yOffset += dy;
                    break;
                case LEFT:
                    dx = -1;
                    xOffset += dx;
                    break;
                case RIGHT:
                    dx = 1;
                    xOffset += dx;
                    break;
            }
            view.shiftView(dx, dy);

        }
        redraw();
    }
    private Point mouseToPoint(int x, int y)
    {
        return new Point((x/TILE_WIDTH)+xOffset, (y/TILE_WIDTH)+yOffset);
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                              imageStore.getImageList(
                                                     DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private static void loadImages(
            String filename, ImageStore imageStore, PApplet screen)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, screen);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void loadWorld(
            WorldModel world, String filename, ImageStore imageStore)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.load(in, world, imageStore);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof  Actionable) ((Actionable)entity).scheduleActions(scheduler, world, imageStore);
        }
    }

    public static void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
