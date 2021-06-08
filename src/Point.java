public final class Point
{
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point)other).x == this.x
                && ((Point)other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }
    public boolean adjacent(Point p1) {
        return (p1.x == this.x && Math.abs(p1.y - this.y) == 1) || (p1.y == this.y
                && Math.abs(p1.x - this.x) == 1);
    }
    public double dist(Point p1) {
        return Math.hypot(this.x-p1.x, this.y-p1.y);
    }
}
