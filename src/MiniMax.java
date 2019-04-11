import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;


public class MiniMax implements Callable<Integer> {

  private int maxDepth;
  private int player;
  private int oppPlayer;
  private Heuristic heuristic;
  AmazonsRules rules;

  public MiniMax(AmazonsRules rules, Move move, Heuristic heuristic, int player, int oppPlayer, int maxDepth) {
    this.maxDepth = maxDepth;
    this.player = player;
    this.oppPlayer = oppPlayer;
    this.heuristic = heuristic;
    this.rules = rules.getCopy();
    this.rules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
  }
//  public MiniMaxMobility( AmazonsRules rules, Move move, int player, int oppPlayer, int maxDepth ) {
//    this.maxDepth = maxDepth;
//    this.player = player;
//    this.oppPlayer = oppPlayer;
//    this.rules = rules.getCopy();
//    this.rules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
//  }

  public Integer call(){
   return miniMax(rules, 0);
  }

  private int miniMax(AmazonsRules state, int depth){
    state.setNextTurnHolder();
    if(depth++ == maxDepth || state.getState().isGameOver()) {
      heuristic.setState(state);
      return heuristic.evaluate();
    }
    if(state.getState().getTurnHolder() == player){
      return getMax(state, depth);
    }else{
      return getMin(state, depth);
    }
  }

  private int getMax (AmazonsRules state, int depth){
    double bestScore = Double.NEGATIVE_INFINITY;

    for (Move move : getMoves(state, player)) {
      AmazonsRules modifiedState = state.getCopy();
      modifiedState.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
      int score = miniMax(modifiedState, depth);

      if (score >= bestScore) {
        bestScore = score;
      }
    }
    return (int)bestScore;
  }

  private int getMin (AmazonsRules state, int depth){
    double bestScore = Double.POSITIVE_INFINITY;

    for (Move move : getMoves(state, oppPlayer)) {
      AmazonsRules modifiedState = state.getCopy();
      modifiedState.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
      int score = miniMax(modifiedState, depth);

      if (score <= bestScore) {
        bestScore = score;
      }
    }
    return (int)bestScore;
  }

  private java.util.List<Move> getMoves(AmazonsRules state, int player) {
    AmazonsState stateCopy = state.getState();
    List<Move> moves = new LinkedList<>();

    for (Point piece : stateCopy.getPieces(player)) {
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
              if (state.canMove(fromX, fromY, toX, toY, shootX, shootY)) {
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
