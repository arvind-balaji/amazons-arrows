import netgame.client.Client;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class MiniMaxAIClientListenerC extends AIClientListener {
  static int wins = 0;
  static int plays = 0;
  private int moveCount = 0;

  public MiniMaxAIClientListenerC(String name) {
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

    System.out.print("\n" + "Move " + moveCount + "\n");
    System.out.print("Evaluating");

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
    List<Future<Integer[]>> resultList = new ArrayList<>();

    for (Move move : bestMoves.getMoves()) {
      EvaluationThread evaluation = new EvaluationThread(rules, move, player, oppPlayer, size);
      Future<Integer[]> score = executor.submit(evaluation);

      resultList.add(score);
    }
    for (int i = 0; i < resultList.size(); i++) {
      try {
        bestMoves.getMoves().get(i).addScores(resultList.get(i).get()[0], resultList.get(i).get()[1]);
        System.out.print(".");
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    //shut down the executor service now
    executor.shutdown();
    while (!executor.isTerminated()) {
    }
    System.out.println();
    bestMoves.normalize(1, 1, 1);


//    System.out.println(bestMoves);
    System.out.println(bestMoves.getBestMove());
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
//    System.out.println(new DecimalFormat("##.##").format(winRate) + "%");
//    System.out.println("Game: " + plays);
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

  class EvaluationThread implements Callable<Integer[]> {
    private int player;
    private int oppPlayer;
    AmazonsRules rules;
    Move move;
    int size;

    public EvaluationThread(AmazonsRules rules, Move move, int player, int oppPlayer, int size) {
      this.player = player;
      this.oppPlayer = oppPlayer;
      this.rules = rules;
      this.size = size;
      this.move = move;
    }

    public Integer[] call() throws Exception {
      int depthA;
      int depthB;
      if (size < 50) {
        depthA = 3;
        depthB = 2;
      } else if (size < 200) {
        depthA = 2;
        depthB = 1;
      } else if (size < 500) {
        depthA = 2;
        depthB = 1;
      } else {
        depthA = 1;
        depthB = 0;
      }
      Heuristic mobilityHeuristic = new MobilityHeuristicB(rules, move, player, oppPlayer);
      Heuristic moveCountHeuristic = new MoveCountHeuristic(rules, move, player, oppPlayer);
      MiniMax miniMaxA = new MiniMax(rules, move, mobilityHeuristic, player, oppPlayer, depthA);
      MiniMax miniMaxB = new MiniMax(rules, move, moveCountHeuristic, player, oppPlayer, depthB);
      return new Integer[]{miniMaxA.call(), miniMaxB.call()};
    }
  }

}

