import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Move implements Comparable<Move> {
  public int fromX;
  public int fromY;
  public int toX;
  public int toY;
  public int shootX;
  public int shootY;

  private List<Integer> scores;


  public Move(int fromX, int fromY, int toX, int toY, int shootX, int shootY) {
    this.fromX = fromX;
    this.fromY = fromY;
    this.toX = toX;
    this.toY = toY;
    this.shootX = shootX;
    this.shootY = shootY;
    this.scores = new ArrayList<>();
  }

  public void setScores(List<Integer> scores) {
    this.scores = scores;
  }

  public int getScore() {
    int total = 0;
    for (int x : this.scores) {
      total += x;
    }
    return total / scores.size();
  }

  public void addScore(int score) {
    this.scores.add(score);
  }

  public void addScores(int... scores) {
    for(int score : scores)
      this.scores.add(score);
  }

  public void removeScore() {
    scores = new ArrayList<>();
  }


  public List<Integer> getScores() {
    return scores;
  }

  public void changeScore(int i, int score) {
    this.scores.set(i, score);
  }

  public String getMoveString() {
    return fromX + C.SPACE + fromY + C.SPACE + toX + C.SPACE + toY + C.SPACE + shootX + C.SPACE + shootY;
  }

  @Override
  public String toString() {
    return "Move{" +
            "score=" + scores +
            '}';
  }

  @Override
  public int compareTo(Move o) {
    return Integer.compare(this.getScore(), o.getScore());
  }
}
