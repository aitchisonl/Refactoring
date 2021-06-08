import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new ArrayList<>();
        PriorityQueue<Node> openList = new PriorityQueue<Node>();
        HashMap<Point, Node> pointMap = new HashMap<Point, Node>();
        Node currentNode = new Node(start, null, 0);
        pointMap.put(start, currentNode);
        openList.add(currentNode);
        while (openList.peek() != null) {
            currentNode = openList.peek();
            if (withinReach.test(currentNode.getPos(), end)) {
                System.out.println("Path found");
                Node endNode = new Node(end, currentNode, 1000);
                return reconstructPath(endNode);
            }
            openList.remove();
            ArrayList<Point> neighbors = (ArrayList<Point>)potentialNeighbors.apply(currentNode.getPos()).filter(canPassThrough).collect(Collectors.toList());
            for (Point p : neighbors) {
                if (!pointMap.containsKey(p)) {
                    pointMap.put(p, new Node(p, currentNode, 1000000));
                }
                int possibleGScore = currentNode.getgScore() + 1;
                Node currentNeighbor = pointMap.get(p);
                if (possibleGScore < currentNeighbor.getgScore()) {
                    currentNeighbor.setPrevNode(currentNode);
                    currentNeighbor.setgScore(possibleGScore);
                    currentNeighbor.setfScore(currentNeighbor.getgScore()+weirdDistance(currentNeighbor.getPos(),end));
                    if (!openList.contains(currentNeighbor)) {
                        openList.add(currentNeighbor);
                    }
                }
            }


        }
        System.out.println("Path not found");
        return path;
    }
    private Integer weirdDistance(Point p1, Point p2) {
        return (Math.abs(p1.x-p2.x) + Math.abs(p1.y-p2.y));
    }

    private ArrayList<Point> reconstructPath(Node current) {
        ArrayList<Point> returnPath = new ArrayList<Point>();
        while (current.getPrevNode() != null) {
            current = current.getPrevNode();
            returnPath.add(0, current.getPos());
        }
        returnPath.remove(0);
        return returnPath;
    }
}
