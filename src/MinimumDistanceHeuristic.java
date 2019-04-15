import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class MinimumDistanceHeuristic extends Heuristic {
  private List<Point> openList;
  private List<Point> closedList;

  public MinimumDistanceHeuristic(AmazonsRules rules, Move move, int player, int oppPlayer) {
    super(rules, move, player, oppPlayer);
    closedList = new LinkedList<Point>();
    openList = new LinkedList<Point>();
  }

  public int evaluate() {
    Point[] pieces = rules.getState().getPieces(player);
    Node<Point> parent = new Node<Point>(pieces[0]);
    populate(parent, new Point(5, 5));
    return 0;
  }

//  private int minDis(Point from, Point to, int i){
//    List<Point> neighbors = getNeighbors(player, from);
//    if(neighbors.size() < 1)
//      return -1;
//    for (Point neighbor : neighbors){
//      if(neighbor.equals(to))
//        return i+1;
//      else
//        return minDis(neighbor, to, i+1);
//    }
//    return 0;
//  }


  private void populate(Node parent, Point to) {
    List<Point> neighbors = getNeighbors(player, (Point) parent.getData());
    if (neighbors.size() == 0) {
      return;
    }
    for (Point point : neighbors) {
      Node<Point> child = new Node<>(point);
      parent.addChild(child);
      populate(child, to);
    }
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
