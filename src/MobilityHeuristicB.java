import java.awt.*;

public class MobilityHeuristicB extends Heuristic {
  public MobilityHeuristicB(AmazonsRules rules, Move move, int player, int oppPlayer) {
    super(rules, move, player, oppPlayer);
  }

  public MobilityHeuristicB(AmazonsRules rules, int player, int oppPlayer) {
    super(rules, player, oppPlayer);
  }

  public int evaluate() {
    return oppMobility() + playerMobility();
  }

  public int oppMobility() {
    double score = 0;
    AmazonsRules rulesCopy = rules.getCopy();
    rulesCopy.getState().setTurnHolder(oppPlayer);

    for (Point piece : rulesCopy.getState().getPieces(oppPlayer))
      for (int dx = -2; dx <= 2; dx++)
        for (int dy = -2; dy <= 2; dy++)
          if ((piece != null) && rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            score--;
    return (int) Math.rint(score);
  }

  public int playerMobility() {
    double score = 0;
    AmazonsRules rulesCopy = rules.getCopy();
    rulesCopy.getState().setTurnHolder(player);

    for (Point piece : rulesCopy.getState().getPieces(player))
      for (int dx = -2; dx <= 2; dx++)
        for (int dy = -2; dy <= 2; dy++)
          if ((piece != null) && rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            score++;
    return (int) Math.rint(score);
  }


}
