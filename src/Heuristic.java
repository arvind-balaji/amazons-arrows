import java.awt.*;
import java.util.LinkedList;
import java.util.List;

abstract class Heuristic {
  protected AmazonsRules rules;
  protected int player;
  protected int oppPlayer;

  public Heuristic(AmazonsRules rules, Move move, int player, int oppPlayer) {
    this.rules = rules.getCopy();
    this.rules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
    this.player = player;
    this.oppPlayer = oppPlayer;
  }

  public Heuristic(AmazonsRules rules, int player, int oppPlayer) {
    this.rules = rules.getCopy();
    this.player = player;
    this.oppPlayer = oppPlayer;
  }

  public void setState(AmazonsRules rules) {
    this.rules = rules.getCopy();
  }

  abstract int evaluate();

  public List<Move> getMoves(AmazonsRules rules, int playerid) {
    AmazonsState state = rules.getState();
    state.setTurnHolder(playerid);
    List<Move> moves = new LinkedList<>();

    for (Point piece : state.getPieces(playerid)) {
      if (null == piece) {
        System.out.println("Missing piece...");
        continue;
      }
      int fromX = piece.x;
      int fromY = piece.y;
      for (int toX = 0; toX < 10; toX++) {
        for (int toY = 0; toY < 10; toY++) {
          for (int shootX = 0; shootX < 10; shootX++) {
            for (int shootY = 0; shootY < 10; shootY++) {
              if (rules.canMove(fromX, fromY, toX, toY, shootX, shootY)) {
                moves.add(new Move(fromX, fromY, toX, toY, shootX, shootY));
              }
            }
          }
        }
      }
    }
    return moves;
  }

  public int[][] getMatrix(AmazonsRules rules) {
    AmazonsState state = rules.getState();
    int[][] ret = new int[10][10];
    for (int x = 0; x < 10; x++) {
      for (int y = 0; y < 10; y++) {
        ret[x][y] = rules.getState().getObjectAt(x, y);
      }
    }
    return ret;
  }
}
