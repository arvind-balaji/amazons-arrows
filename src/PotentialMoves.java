import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PotentialMoves {
  private List<Move> moves;

  public PotentialMoves(List<Move> moves) {
    this.moves = moves;
  }


  public List<Move> getMoves() {
    return moves;
  }

  public Move getBestMove() {
    Collections.shuffle(moves);
    Collections.sort(moves);
    Collections.reverse(moves);
    return moves.get(0);
  }

  public List<Move> getBestMoves(int num) {
    num = num > moves.size() - 1 ? moves.size() : num;
    List<Move> ret = new ArrayList<>();
    Collections.shuffle(moves);
    Collections.sort(moves);
    Collections.reverse(moves);
    for (int i=0; i<num; i++ ) {
      Move move = moves.get(i);
      Move moveCopy = new Move(move.fromX,move.fromY,move.toX,move.toY,move.shootX,move.shootY);
      moveCopy.setScores(move.getScores());
      ret.add(moveCopy);
    }
    return ret;
  }


  public void normalize() {
    if(moves.size() < 1){
      return;
    }
    for (int i = 0; i < moves.get(0).getScores().size(); i++) {
      normalizeMove(i, 1);
    }
  }

  public void normalize(double... weights) {
    if(moves.size() < 1){
      return;
    }
    for (int i = 0; i < moves.get(0).getScores().size(); i++) {
      normalizeMove(i, weights[i]);
    }
  }

  public void normalizeMove(int index, double weight) {
    double max = 0;
    for (Move move : this.moves) {
      int _score = Math.abs((move.getScores().get(index)));
      max = _score > max ? _score : max;
    }
    for (Move move : this.moves) {
      int normScore = (int)
                      (( Math.abs((double) move.getScores().get(index) / max)) * // get ratio of score to max score
                      (100 * weight) * // scale score
                      (Integer.signum( move.getScores().get(index)))); // put correct sign

      move.changeScore(index, normScore);
    }
  }

  public void removeScores() {
    for (Move move: moves) {
      move.removeScore();
    }
  }


  @Override
  public String toString() {
    return "PotentialMoves{" +
            "moves=" + moves +
            '}';
  }


}
