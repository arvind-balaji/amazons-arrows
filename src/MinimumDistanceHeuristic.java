import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.awt.*;

public class MinimumDistanceHeuristic extends Heuristic {
  private List<java.awt.Point> openList;
  private List<java.awt.Point> closedList;

  public MinimumDistanceHeuristic(AmazonsRules rules, Move move, int player, int oppPlayer) {
    super(rules, move, player, oppPlayer);
    closedList = new LinkedList<java.awt.Point>();
    openList = new LinkedList<java.awt.Point>();
  }

  public int evaluate() {
    int accessibleTilesCount = 0;
    int oppAccessibleTilesCount = 0;
    java.awt.Point[] pieces = rules.getState().getPieces(player);
    for (Point piece : pieces) {
      for (int x = 0; x < 10; x++) {
        for (int y = 0; y < 10; y++)
          if (rules.getState().getObjectAt(x, y) == 0) {
            for (Point reachable : getNeighbors(player, piece)) {
              if (reachable.equals(new Point(x, y))) {
                accessibleTilesCount++;
                break;
              }
            }
          }
      }
    }
    pieces = rules.getState().getPieces(oppPlayer);
    for (Point piece : pieces) {
      for (int x = 0; x < 10; x++) {
        for (int y = 0; y < 10; y++) {
          if (rules.getState().getObjectAt(x, y) == 0) {
            for (Point reachable : getNeighbors(oppPlayer, piece)) {
              if (reachable.equals(new Point(x, y)))
                oppAccessibleTilesCount++;
            }
          }
        }
      }
    }
    return accessibleTilesCount - oppAccessibleTilesCount;
  }

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
//    return 0;
//  }


  private double euclidanDistance(java.awt.Point from, java.awt.Point to) {
    double x = Math.pow(to.getX() - from.getX(), 2.0);
    double y = Math.pow(to.getX() - from.getX(), 2.0);

    return x + y;
  }

  private List<java.awt.Point> getNeighbors(int player, java.awt.Point src) {
    LinkedList<java.awt.Point> ret = new LinkedList<>();
    for (java.awt.Point point : getMovesInDirection(player, src, 1, 0))
      ret.add(point);
    for (java.awt.Point point : getMovesInDirection(player, src, -1, 0))
      ret.add(point);
    for (java.awt.Point point : getMovesInDirection(player, src, 0, 1))
      ret.add(point);
    for (java.awt.Point point : getMovesInDirection(player, src, 0, -1))
      ret.add(point);
    for (java.awt.Point point : getMovesInDirection(player, src, 1, 1))
      ret.add(point);
    for (java.awt.Point point : getMovesInDirection(player, src, 1, -1))
      ret.add(point);
    for (java.awt.Point point : getMovesInDirection(player, src, -1, 1))
      ret.add(point);
    for (java.awt.Point point : getMovesInDirection(player, src, -1, -1))
      ret.add(point);

    return ret;

  }

  private List<java.awt.Point> getMovesInDirection(int player, java.awt.Point src, int dx, int dy) {
    List ret = new LinkedList<java.awt.Point>();
    AmazonsRules rulesCopy = this.rules.getCopy();
    rulesCopy.getState().setTurnHolder(player);
    java.awt.Point currPoint = src;
    while (rulesCopy.canMove(src.x, src.y, currPoint.x + dx, currPoint.y + dy)) {
      currPoint = new java.awt.Point(currPoint.x + dx, currPoint.y + dy);
      ret.add(currPoint);
    }
    return ret;
  }
}
