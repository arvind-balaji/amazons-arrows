import java.util.*;
import java.awt.Point;
import java.util.stream.*;


import netgame.client.Client;

public class MyAIClientListener extends AIClientListener {

  public MyAIClientListener(String name) {
    super(name);
  }

  @Override
  public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
    List<int[]> moves = getMoves(rules, this.getMyPlayerNumber());

    Map scoredMoves = new LinkedHashMap();

    for (int[] move : moves) {
      AmazonsRules newRules = rules.getCopy();
      newRules.move(move[0], move[1], move[2], move[3], move[4], move[5]);
      scoredMoves.put(move, scoreState(newRules));
    }
    Optional<Map.Entry<int[], Integer>> bestMove =
            scoredMoves.entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .findFirst();
    int[] myMove = bestMove.get().getKey();
    client.send(C.MOVE + C.SPACE + myMove[0] + C.SPACE + myMove[1] + C.SPACE + myMove[2] + C.SPACE + myMove[3] + C.SPACE + myMove[4] + C.SPACE + myMove[5]);
  }

  @Override
  public void gameover(String reason) {
    System.out.println("you are: Player " + this.getMyPlayerNumber());
    System.out.println("gameover: " + reason);
  }

  private int scoreState(AmazonsRules rules) {
    List<int[]> myMoves = getMoves(rules, this.getMyPlayerNumber());
    List<int[]> opponentMoves = getMoves(rules, this.getOtherPlayerNumber());
    return myMoves.size() - opponentMoves.size();
  }


  private List<int[]> getMoves(AmazonsRules rules, int player) {
    AmazonsState state = rules.getState();
    List<int[]> moves = new LinkedList<>();

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
                moves.add(new int[]{fromX, fromY, toX, toY, shootX, shootY});
              }
            }
          }
        }
      }
    }
    return moves;
  }
}

