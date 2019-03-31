import java.util.*;
import java.awt.Point;


import netgame.client.Client;

public class MyAIClientListener extends AIClientListener {

  public MyAIClientListener(String name) {
    super(name);
  }

  @Override
  public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
    PotentialMoves potentialMoves = new PotentialMoves(getMoves(rules, this.getMyPlayerNumber()));

    for (Move move : potentialMoves.getMoves()) {
      AmazonsRules newRules = rules.getCopy();
      newRules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
      move.addScore(moveCountScore(newRules));
      move.addScore(arrowProximityScore(rules, move));
      move.addScore(playerProximityScore(rules, move));
    }

    potentialMoves.normalize(new double[]{1, 1, .5});
    System.out.println(potentialMoves);
    System.out.println(potentialMoves.getBestMove());
    client.send(C.MOVE + C.SPACE + potentialMoves.getBestMove().getMoveString());
  }

  @Override
  public void gameover(String reason) {
    System.out.println("you are: Player " + this.getMyPlayerNumber());
    System.out.println("gameover: " + reason);
  }

  private int moveCountScore(AmazonsRules rules) {
    List<Move> myMoves = getMoves(rules, this.getMyPlayerNumber());
    List<Move> opponentMoves = getMoves(rules, this.getOtherPlayerNumber());
    return myMoves.size() - opponentMoves.size();
  }

  private int arrowProximityScore(AmazonsRules rules, Move move) {
    int score = 0;
    for (Point piece : rules.getState().getPieces(this.getOtherPlayerNumber())) {
      int xDiff = Math.abs(piece.x - move.shootX);
      int yDiff = Math.abs(piece.y - move.shootY);
      if (xDiff <= 1 && yDiff <= 1) {
        score += 100;
      } else {
        score += 100 / (xDiff + yDiff);
      }
    }
    for (Point piece : rules.getState().getPieces(this.getMyPlayerNumber())) {
      int xDiff = Math.abs(piece.x - move.shootX);
      int yDiff = Math.abs(piece.y - move.shootY);
      if (xDiff <= 1 && yDiff <= 1) {
        score = 0;
      } else {
        score += 10 * (xDiff + yDiff);
      }

    }
    return score;
  }

  private int playerProximityScore(AmazonsRules rules, Move move) {
    int score = 0;
//    for (Point piece : rules.getState().getPieces(this.getOtherPlayerNumber())) {
//      int xDiff = Math.abs(piece.x - move.toX);
//      int yDiff = Math.abs(piece.y - move.toY);
//      score+=100*(xDiff+yDiff-2);
//    }
    for (Point piece : rules.getState().getPieces(this.getMyPlayerNumber())) {
      int xDiff = Math.abs(piece.x - move.toX);
      int yDiff = Math.abs(piece.y - move.toY);
      score += 1000 / (xDiff + yDiff);
    }
    return score;
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

