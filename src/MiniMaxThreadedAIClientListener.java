import netgame.client.Client;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class MiniMaxThreadedAIClientListener extends AIClientListener {
  static int wins = 0;
  static int plays = 0;
  private static double maxPly = 2;
  public MiniMaxThreadedAIClientListener(String name) {
    super(name);
  }

  @Override
  public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
    PotentialMoves moves = new PotentialMoves(getMoves(rules, this.getMyPlayerNumber()));
    for (Move move : moves.getMoves()) {
      move.addScore(moveCountScore(rules,move));
      move.addScore(playerMobilityScore(rules, move));
      move.addScore(opponentMobilityScore(rules, move));
    }
    moves.normalize();
    PotentialMoves bestMoves = new PotentialMoves(moves.getBestMoves(10));
    if(moves.getMoves().size()<20){
      bestMoves.removeScores();
      for (Move move : bestMoves.getMoves()) {
//        AmazonsRules modifiedState = rules.getCopy();
//        modifiedState.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
//        int score = miniMax(this.getMyPlayerNumber(), modifiedState, 0);
//        move.addScore(score);
//        move.addScore(moveCountScore(rules,move));
      }
      bestMoves.normalize();
    }

    System.out.println(bestMoves);
    client.send(C.MOVE + C.SPACE + bestMoves.getBestMove().getMoveString());
  }

  @Override
  public void gameover(String reason) {
    plays++;
    if(Integer.parseInt(reason.split(" ")[1])==this.getMyPlayerNumber()){
      wins++;
    }
    double winRate = ((double)wins /(double)plays)*100;
    System.out.println(new DecimalFormat("##.##").format(winRate) + "%");
  }


  private int miniMax(int player, AmazonsRules state, int depth){
    state.setNextTurnHolder();
    if(depth++ == maxPly || state.getState().isGameOver()) {
      return score_mobility(state);
    }
    if(state.getState().getTurnHolder() == player){
      return getMax(player, state, depth);
    }else{
      return getMin(player, state, depth);
    }
  }

  private int getMax (int player, AmazonsRules state, int depth){
    double bestScore = Double.NEGATIVE_INFINITY;

    for (Move move : getMoves(state, this.getMyPlayerNumber())) {
     AmazonsRules modifiedState = state.getCopy();
     modifiedState.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
     int score = miniMax(player, modifiedState, depth);

     if (score >= bestScore) {
       bestScore = score;
     }
    }
    return (int)bestScore;
  }

  private int getMin (int player, AmazonsRules state, int depth){
    double bestScore = Double.POSITIVE_INFINITY;

    for (Move move : getMoves(state, this.getOtherPlayerNumber())) {
      AmazonsRules modifiedState = state.getCopy();
      modifiedState.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
      int score = miniMax(player, modifiedState, depth);

      if (score <= bestScore) {
        bestScore = score;
      }
    }
    return (int)bestScore;
  }

  private int moveCountScore(AmazonsRules rules, Move move) {
    AmazonsRules newRules = rules.getCopy();
    newRules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
    List<Move> myMoves = getMoves(newRules, this.getMyPlayerNumber());

    newRules.getState().setTurnHolder(this.getOtherPlayerNumber());
    List<Move> opponentMoves = getMoves(newRules, this.getOtherPlayerNumber());

    return myMoves.size() - opponentMoves.size();
  }

  private int score_mobility(AmazonsRules rules) {
    AmazonsRules state = rules.getCopy();
    state.getState().setTurnHolder(this.getOtherPlayerNumber());
    int score = 0;
    int opMobility = 0;

    for (Point piece : state.getState().getPieces(this.getOtherPlayerNumber()))
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
          if (state.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            opMobility++;

    state.getState().setTurnHolder(this.getMyPlayerNumber());
    int myMobility = 0;
    for (Point piece : state.getState().getPieces(this.getMyPlayerNumber()))
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
          if (state.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            myMobility++;

    return myMobility-opMobility;
  }

  private int score_movecount(AmazonsRules rules) {
    AmazonsRules state = rules.getCopy();
    state.getState().setTurnHolder(this.getMyPlayerNumber());
    List<Move> myMoves = getMoves(state, this.getMyPlayerNumber());
    state.getState().setTurnHolder(this.getOtherPlayerNumber());
    state.getState().setTurnHolder(this.getOtherPlayerNumber());
    List<Move> opponentMoves = getMoves(state, this.getOtherPlayerNumber());

    return myMoves.size() - opponentMoves.size();
  }
  private int arrowProximityScore(AmazonsRules rules, Move move) {
    int score = 0;
//    for (Point piece : rules.getState().getPieces(this.getOtherPlayerNumber())) {
//      int xDiff = Math.abs(piece.x - move.shootX);
//      int yDiff = Math.abs(piece.y - move.shootY);
////      if (xDiff <= 1 && yDiff <= 1) {
////        score += 100;
////      } else {
//        score += 1000 / (xDiff + yDiff);
////      }
//    }

    for (Point piece : rules.getState().getPieces(this.getMyPlayerNumber())) {
      int xDiff = Math.abs(piece.x - move.shootX);
      int yDiff = Math.abs(piece.y - move.shootY);
      if (xDiff <= 1 && yDiff <= 1) {
        return 0;
      } else {
        score += 10 * (xDiff + yDiff);
      }

    }
    return score;
  }







  private int playerProximityScore(AmazonsRules rules, Move move) {
    int score = 0;
    for (Point piece : rules.getState().getPieces(this.getMyPlayerNumber())) {
      int xDiff = Math.abs(piece.x - move.toX);
      int yDiff = Math.abs(piece.y - move.toY);
      score += 1000 / (xDiff + yDiff);
    }
    return score;
  }

  private int opponentMobilityScore(AmazonsRules rules, Move move) {
    AmazonsRules rulesCopy = rules.getCopy();
    rulesCopy.setNextTurnHolder();
    int mobility = 0;
    int newMobility = 0;

    for (Point piece : rulesCopy.getState().getPieces(this.getOtherPlayerNumber()))
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
          if (rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            mobility++;

    rulesCopy.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);

    for (Point piece : rulesCopy.getState().getPieces(this.getOtherPlayerNumber()))
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
          if (rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            newMobility++;

    if (newMobility > mobility)
      return 0;
    else if (newMobility == mobility)
      return 5;
    else
      return (mobility - newMobility) * 10;
  }

  private int playerMobilityScore(AmazonsRules rules, Move move) {
    AmazonsRules rulesCopy = rules.getCopy();

    int mobility = 0;
    int newMobility = 0;

    for (Point piece : rulesCopy.getState().getPieces(this.getMyPlayerNumber()))
      for (int dx = -2; dx <= 2; dx++)
        for (int dy = -2; dy <= 2; dy++)
          if (rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            mobility++;

    rulesCopy.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);

    for (Point piece : rulesCopy.getState().getPieces(this.getMyPlayerNumber()))
      for (int dx = -2; dx <= 2; dx++)
        for (int dy = -2; dy <= 2; dy++)
          if (rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            newMobility++;

    if (newMobility < mobility)
      return 0;
    else if (newMobility == mobility)
      return 5;
    else
      return (newMobility - mobility) * 10;
  }

  private int randomScore() {
    return (int)(Math.random() * 10);
  }

  private List<Move> getMoves(AmazonsRules rules, int player) {
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

