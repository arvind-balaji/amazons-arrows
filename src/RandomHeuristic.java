import java.util.List;

public class RandomHeuristic extends Heuristic {
  public RandomHeuristic(AmazonsRules rules, Move move, int player, int oppPlayer) {
    super(rules, move, player, oppPlayer);
  }

  public int evaluate() {
    return (int) (Math.random() * 10);
  }
}
