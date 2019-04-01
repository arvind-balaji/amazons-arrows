import java.util.*;
import java.awt.Point;


import netgame.client.Client;

public class MyAIClientListenerB extends AIClientListener {

  public MyAIClientListenerB(String name) {
    super(name);
  }

  @Override
  public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
    PotentialMoves potentialMoves = new PotentialMoves(getMoves(rules, this.getMyPlayerNumber()));

    for (Move move : potentialMoves.getMoves()) {
//      move.addScore(moveCountScore(rules, move));
//      move.addScore(arrowProximityScore(rules, move));
//      move.addScore(playerProximityScore(rules, move));
      move.addScore(playerMobilityScore(rules, move));
      move.addScore(opponentMobilityScore(rules, move));
      move.addScore(randomScore());

    }

    potentialMoves.normalize(1, 1, .25);

//    System.out.println(potentialMoves);
    System.out.println(potentialMoves.getBestMove());
    client.send(C.MOVE + C.SPACE + potentialMoves.getBestMove().getMoveString());
  }

  @Override
  public void gameover(String reason) {
//    System.out.println("you are: Player " + this.getMyPlayerNumber());
    System.out.println("gameover: " + reason);
  }

  private int moveCountScore(AmazonsRules rules, Move move) {
    AmazonsRules newRules = rules.getCopy();
    newRules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);

    List<Move> myMoves = getMoves(newRules, this.getMyPlayerNumber());
    List<Move> opponentMoves = getMoves(newRules, this.getOtherPlayerNumber());

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
//    System.out.print(score + " -> ");
    for (Point piece : rules.getState().getPieces(this.getMyPlayerNumber())) {
      int xDiff = Math.abs(piece.x - move.shootX);
      int yDiff = Math.abs(piece.y - move.shootY);
      if (xDiff <= 1 && yDiff <= 1) {
        return 0;
      } else {
        score += 10 * (xDiff + yDiff);
      }

    }
//    System.out.print(score + "\n");
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
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
          if (rulesCopy.canMove(piece.x, piece.y, piece.x + dx, piece.y + dy))
            mobility++;

    rulesCopy.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);

    for (Point piece : rulesCopy.getState().getPieces(this.getMyPlayerNumber()))
      for (int dx = -1; dx <= 1; dx++)
        for (int dy = -1; dy <= 1; dy++)
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
    return (int) Math.random() * 10;
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

