import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class MoveCountHeuristic extends Heuristic {
  public MoveCountHeuristic(AmazonsRules rules, Move move, int player, int oppPlayer) {
    super(rules, move, player, oppPlayer);
  }

  public int evaluate() {
    AmazonsRules rulesCopy = rules.getCopy();

    rulesCopy.getState().setTurnHolder(player);
    List<Move> myMoves = getMoves(rulesCopy, player);

    rulesCopy.getState().setTurnHolder(oppPlayer);
    List<Move> opponentMoves = getMoves(rulesCopy, oppPlayer);

    return myMoves.size() - opponentMoves.size();
  }
}
