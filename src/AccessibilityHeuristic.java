import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class AccessibilityHeuristic extends Heuristic {
  private List<Point> openList;
  private List<Point> closedList;

  public AccessibilityHeuristic(AmazonsRules rules, Move move, int player, int oppPlayer) {
    super(rules, move, player, oppPlayer);
    closedList = new LinkedList<Point>();
    openList = new LinkedList<Point>();
  }

  public int evaluate() {
//    java.awt.Point[] pieces = rules.getState().getPieces(player);
//    Point<Point> parent = new Point<Point>(pieces[0]);
//    PriorityQueue frontier = new PriorityQueue();
//  }
//
//  private int minDis(java.awt.Point from, java.awt.Point to, int i){
//    List<java.awt.Point> neighbors = getNeighbors(player, from);
//    if(neighbors.size() < 1)
//      return -1;
//    for (java.awt.Point neighbor : neighbors){
//      if(neighbor.equals(to))
//        return i+1;
//      else
//        return minDis(neighbor, to, i+1);
//    }
    return 0;
  }


  private void populate(Point parent, Point to) {

  }

  private double euclidanDistance(Point from, Point to) {
    double x = Math.pow(to.getX() - from.getX(), 2.0);
    double y = Math.pow(to.getX() - from.getX(), 2.0);

    return x + y;
  }

  private List<Point> getNeighbors(int player, Point src) {
    LinkedList<Point> ret = new LinkedList<>();
    for (Point point : getMovesInDirection(player, src, 1, 0))
      ret.add(point);
    for (Point point : getMovesInDirection(player, src, -1, 0))
      ret.add(point);
    for (Point point : getMovesInDirection(player, src, 0, 1))
      ret.add(point);
    for (Point point : getMovesInDirection(player, src, 0, -1))
      ret.add(point);
    for (Point point : getMovesInDirection(player, src, 1, 1))
      ret.add(point);
    for (Point point : getMovesInDirection(player, src, 1, -1))
      ret.add(point);
    for (Point point : getMovesInDirection(player, src, -1, 1))
      ret.add(point);
    for (Point point : getMovesInDirection(player, src, -1, -1))
      ret.add(point);

    return ret;

  }

  private List<Point> getMovesInDirection(int player, Point src, int dx, int dy) {
    List ret = new LinkedList<Point>();
    AmazonsRules rulesCopy = this.rules.getCopy();
    rulesCopy.getState().setTurnHolder(player);
    Point currPoint = src;
    while (rulesCopy.canMove(src.x, src.y, currPoint.x + dx, currPoint.y + dy)) {
      currPoint = new Point(currPoint.x + dx, currPoint.y + dy);
      ret.add(currPoint);
    }
    return ret;
  }
}
