import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.awt.Point;


import netgame.client.Client;

public class MyAIClientListenerB extends AIClientListener {
  static int wins = 0;
  static int plays = 0;
  public MyAIClientListenerB(String name) {
    super(name);
  }

  @Override
  public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
    PotentialMoves potentialMoves = new PotentialMoves(getMoves(rules, this.getMyPlayerNumber()));
    if (potentialMoves.getMoves().size() > 500) {
      for (Move move : potentialMoves.getMoves()) {
        Heuristics heuristic = new Heuristics(rules, move, this.getMyPlayerNumber(), this.getOtherPlayerNumber());
        move.addScores(
                heuristic.oppMobility(),
                heuristic.playerMobility(),
                heuristic.moveCount()
        );
      }
      potentialMoves.normalize(.25,1,1);
    }else{
      for (Move move : potentialMoves.getMoves()) {
        Heuristics heuristic = new Heuristics(rules, move, this.getMyPlayerNumber(), this.getOtherPlayerNumber());
        MiniMax miniMax = new MiniMax(rules, move, this.getMyPlayerNumber(), this.getOtherPlayerNumber(), 2);
        move.addScores(
                heuristic.randomScore(),
                miniMax.call()
        );
      }
      potentialMoves.normalize(.25,1,1);
    }


    System.out.println(potentialMoves);
//    System.out.println(potentialMoves.getBestMove());
    client.send(C.MOVE + C.SPACE + potentialMoves.getBestMove().getMoveString());
  }

  @Override
  public void gameover(String reason) {
    plays++;
    if(Integer.parseInt(reason.split(" ")[1])==this.getMyPlayerNumber()){
      wins++;
    }
//    double winRate = ((double)wins /(double)plays)*100;
//    System.out.println(new DecimalFormat("##.##").format(winRate) + "%");
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

