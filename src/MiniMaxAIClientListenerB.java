import java.text.DecimalFormat;
import java.util.*;
import java.awt.Point;


import netgame.client.Client;

public class MiniMaxAIClientListenerB extends AIClientListener {
  static int wins = 0;
  static int plays = 0;
  private int moveCount = 0;

  public MiniMaxAIClientListenerB(String name) {
    super(name);
  }

  @Override
  public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
    this.moveCount++;
    int player = this.getMyPlayerNumber();
    int oppPlayer = this.getOtherPlayerNumber();

    Heuristic mobilityHeuristic;
    Heuristic moveCountHeuristic;
    Heuristic randomHeuristic;

    PotentialMoves moves = new PotentialMoves(getMoves(rules, this.getMyPlayerNumber()));
    int size = moves.getMoves().size();

    for (Move move : moves.getMoves()) {
      randomHeuristic = new RandomHeuristic(rules, move, player, oppPlayer);
      mobilityHeuristic = new MobilityHeuristicB(rules, move, player, oppPlayer);
      moveCountHeuristic = new MoveCountHeuristic(rules, move, player, oppPlayer);
      move.addScores(
              randomHeuristic.evaluate(),
              mobilityHeuristic.evaluate(),
              moveCountHeuristic.evaluate()
      );
    }
    moves.normalize(.25, 1, 1, 1);

    PotentialMoves bestMoves = new PotentialMoves(moves.getBestMoves(10));
    bestMoves.removeScores();
    int depthA;
    int depthB;
    if (size < 50) {
      depthA = 3;
      depthB = 2;
    } else if (size < 250) {
      depthA = 2;
      depthB = 1;
    } else if (size < 500) {
      depthA = 2;
      depthB = 1;
    } else {
      depthA = 1;
      depthB = 0;
    }
    System.out.print("\n" + "Move " + moveCount + "\n");
    System.out.print("Evaluating");
    for (Move move : bestMoves.getMoves()) {
      mobilityHeuristic = new MobilityHeuristicB(rules, move, player, oppPlayer);
      moveCountHeuristic = new MoveCountHeuristic(rules, move, player, oppPlayer);


//      System.out.println("depth: " + depthA + ", " + depthB);

      MiniMax miniMaxA = new MiniMax(rules, move, mobilityHeuristic, player, oppPlayer, depthA);
      MiniMax miniMaxB = new MiniMax(rules, move, moveCountHeuristic, player, oppPlayer, depthB);
      move.addScores(
              miniMaxA.call(),
              miniMaxB.call()
      );
      System.out.print("..");
    }
    System.out.println();
    bestMoves.normalize(1, 1, 1);


//    System.out.println(bestMoves);
//    System.out.println(bestMoves.getBestMove());
    client.send(C.MOVE + C.SPACE + bestMoves.getBestMove().getMoveString());
  }

  @Override
  public void gameover(String reason) {
    plays++;
    moveCount = 0;
    if (Integer.parseInt(reason.split(" ")[1]) == this.getMyPlayerNumber()) {
      wins++;
    }
    double winRate = ((double) wins / (double) plays) * 100;
    System.out.println(new DecimalFormat("##.##").format(winRate) + "%");
    System.out.println("Game: " + plays);
  }


  private List<Move> getMoves(AmazonsRules rules, int player) {
    AmazonsState state = rules.getState();
    state.setTurnHolder(player);
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

