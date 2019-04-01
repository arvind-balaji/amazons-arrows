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
    return moves.get(moves.size() - 1);
  }

  public void normalize() {
    for (int i = 0; i < moves.get(0).getScores().size(); i++) {
      normalizeMove(i, 0);
    }
  }

  public void normalize(double... weights) {
    for (int i = 0; i < moves.get(0).getScores().size(); i++) {
      normalizeMove(i, weights[i]);
    }
  }

  public void normalizeMove(int index, double weight) {
    double max = 0;
    for (Move move : this.moves) {
      int _score = (move.getScores().get(index));
      max = _score > max ? _score : max;
    }
    for (Move move : this.moves) {
      int normScore = (int) (((double) move.getScores().get(index) / max) * (1000 * weight));
      move.changeScore(index, normScore);
    }
  }

  @Override
  public String toString() {
    return "PotentialMoves{" +
            "moves=" + moves +
            '}';
  }


}
