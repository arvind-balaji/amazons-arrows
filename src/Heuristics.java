import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Heuristics {
  private AmazonsRules rules;
  private int player;
  private int oppPlayer;

  public Heuristics(AmazonsRules rules, int player, int oppPlayer) {
    this.rules = rules;
    this.player = player;
    this.oppPlayer = oppPlayer;
  }
  public Heuristics(AmazonsRules rules, Move move, int player, int oppPlayer) {
    this.rules = rules.getCopy();
    this.player = player;
    this.oppPlayer = oppPlayer;
    this.rules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);

  }

  public int moveCount() {
    AmazonsRules rulesCopy = rules.getCopy();

    rulesCopy.getState().setTurnHolder(player);
    List<Move> myMoves = getMoves(rulesCopy, player);

    rulesCopy.getState().setTurnHolder(oppPlayer);
    List<Move> opponentMoves = getMoves(rulesCopy, oppPlayer);

    return myMoves.size() - opponentMoves.size();
  }


  public int oppMobility() {
    int score = 0;
    AmazonsRules rulesCopy = rules.getCopy();
    rulesCopy.getState().setTurnHolder(oppPlayer);

    for (Point piece : rulesCopy.getState().getPieces(oppPlayer))
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
          if (rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            score--;
    return score;
  }

  public int playerMobility() {
    int score = 0;
    AmazonsRules rulesCopy = rules.getCopy();
    rulesCopy.getState().setTurnHolder(player);

    for (Point piece : rulesCopy.getState().getPieces(player))
      for (int dx = -2; dx <= 2; dx++)
        for (int dy = -2; dy <= 2; dy++)
          if ((piece != null) && rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            score++;
    return score;
  }

  public int randomScore() {
    return (int) (Math.random() * 10);
  }

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
}
