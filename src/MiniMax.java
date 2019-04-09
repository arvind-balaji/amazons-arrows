import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;


public class MiniMax implements Callable<Integer> {

  private int maxDepth;
  private int player;
  private int oppPlayer;
  AmazonsRules rules;

  public MiniMax( AmazonsRules rules, Move move, int player, int oppPlayer, int maxDepth ) {
    this.maxDepth = maxDepth;
    this.player = player;
    this.oppPlayer = oppPlayer;
    this.rules = rules.getCopy();
    this.rules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
  }
  public MiniMax( AmazonsRules rules, int player, int oppPlayer, int maxDepth ) {
    this.maxDepth = maxDepth;
    this.player = player;
    this.oppPlayer = oppPlayer;
    this.rules = rules.getCopy();
  }

  public Integer call(){
   return miniMax(rules, 0);
  }

  private int miniMax(AmazonsRules state, int depth){
    state.setNextTurnHolder();
    if(depth++ == maxDepth || state.getState().isGameOver()) {
      Heuristics heuristic = new Heuristics(state, player, oppPlayer);
      return heuristic.oppMobility() + heuristic.playerMobility() + heuristic.moveCount();
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
  private int score_mobility(AmazonsRules rules) {
    AmazonsRules state = rules.getCopy();
    state.getState().setTurnHolder(oppPlayer);
    int score = 0;
    int opMobility = 0;

    for (Point piece : state.getState().getPieces(oppPlayer))
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
          if (state.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            opMobility++;

    state.getState().setTurnHolder(player);
    int myMobility = 0;
    for (Point piece : state.getState().getPieces(player))
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
          if (state.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            myMobility++;

    return myMobility-opMobility;
  }
  private java.util.List<Move> getMoves(AmazonsRules rules, int player) {
    AmazonsState state = rules.getState();
    List<Move> moves = new LinkedList<>();

    for (Point piece : state.getPieces(player)) {
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
