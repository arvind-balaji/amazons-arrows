import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class MobilityHeuristic extends Heuristic {
  public MobilityHeuristic(AmazonsRules rules, Move move, int player, int oppPlayer) {
    super(rules, move, player, oppPlayer);
  }

  public MobilityHeuristic(AmazonsRules rules, int player, int oppPlayer) {
    super(rules, player, oppPlayer);
  }

  public int evaluate() {
    return oppMobility() + playerMobility();
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


}
