public class Node implements Comparable{

    private Point pos;
    private int gScore;
    private int fScore;
    private int hVal;
    private Node prevNode;

    public Node(Point pos, Node prevNode, int gScore) {
        this.pos = pos;
        this.prevNode = prevNode;
        this.gScore = gScore;
        this.fScore = 10000000;
    }

    public Point getPos() {
        return this.pos;
    }

    public int getgScore() {
        return this.gScore;
    }

    public int getfScore() {
        return fScore;
    }

    public void setgScore(int gScore) {
        this.gScore = gScore;
    }

    public void setfScore(int fScore) {
        this.fScore = fScore;
    }

    public void sethVal(int hVal) {
        this.hVal = hVal;
    }

    public int gethVal() {
        return hVal;
    }

    public Node getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }

    @Override
    public int compareTo(Object o) {
        return this.fScore-((Node)o).getfScore();
    }
}
