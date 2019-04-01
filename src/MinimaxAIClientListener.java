import netgame.client.Client;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MinimaxAIClientListener extends AIClientListener {

  public MinimaxAIClientListener(String name) {
    super(name);
  }

  @Override
  public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
    List<Move> moves = getMoves(rules, this.getMyPlayerNumber());


    for (Move move : moves) {
      AmazonsRules newRules = rules.getCopy();
      newRules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
      move.addScore(minimax(0, newRules, this.getMyPlayerNumber()));
    }
    Collections.sort(moves);
    Move bestMove = moves.get(moves.size() - 1);
    System.out.println(bestMove);
    client.send(C.MOVE + C.SPACE + bestMove.getMoveString());
  }

  @Override
  public void gameover(String reason) {
    System.out.println("you are: Player " + this.getMyPlayerNumber());
    System.out.println("gameover: " + reason);
  }

  private int score(int player, AmazonsRules rules) {
    int otherPlayer = (player == this.getMyPlayerNumber()) ? this.getOtherPlayerNumber() : this.getMyPlayerNumber();
    List<Move> moves = getMoves(rules, player);
    List<Move> otherMoves = getMoves(rules, otherPlayer);
    if (moves.size() == 0) {
      return -10;
    } else if (otherMoves.size() == 0) {
      return 10;
    } else {
      return 0;
    }
  }

  private int arrowProximityScore(AmazonsRules rules, Move move, int player) {
    int otherPlayer = (player == this.getMyPlayerNumber()) ? this.getOtherPlayerNumber() : this.getMyPlayerNumber();
    int score = 0;
    for (Point piece : rules.getState().getPieces(otherPlayer)) {
      int xDiff = Math.abs(piece.x - move.shootX);
      int yDiff = Math.abs(piece.y - move.shootY);
      if (xDiff <= 1 && yDiff <= 1) {
        score += 100;
      } else {
        score += 100 / (xDiff + yDiff);
      }
    }
    for (Point piece : rules.getState().getPieces(player)) {
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


  private int getMax(int depth, AmazonsRules rules, int player) {
    double bestScore = Double.NEGATIVE_INFINITY;
    List<Move> moves = getMoves(rules, player);
    Move bestMove = moves.get(0);

    for (Move move : moves) {
      AmazonsRules newRules = rules.getCopy();
      newRules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
      int score = minimax(depth, newRules, player);
      if (score >= bestScore) {
        bestScore = score;
        bestMove = move;
      }
    }
    System.out.println(moves.size());
    rules.move(bestMove.fromX, bestMove.fromY, bestMove.toX, bestMove.toY, bestMove.shootX, bestMove.shootY);
    return (int) bestScore;
  }

  private int getMin(int depth, AmazonsRules rules, int player) {
    double bestScore = Double.POSITIVE_INFINITY;
    List<Move> moves = getMoves(rules, player);
    Move bestMove = moves.get(0);

    for (Move move : moves) {
      AmazonsRules newRules = rules.getCopy();
      newRules.move(move.fromX, move.fromY, move.toX, move.toY, move.shootX, move.shootY);
      int score = minimax(depth, newRules, player);
      if (score <= bestScore) {
        bestScore = score;
        bestMove = move;
      }
    }
    System.out.println(moves.size());
    rules.move(bestMove.fromX, bestMove.fromY, bestMove.toX, bestMove.toY, bestMove.shootX, bestMove.shootY);
    return (int) bestScore;
  }

  private int minimax(int depth, AmazonsRules rules, int player) {
    if (depth++ == 1) {
      return score(player, rules);
    }
    if (rules.isTurnHolder(player)) {
      return getMax(depth, rules, player);
    } else {
      return getMin(depth, rules, player);
    }
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

