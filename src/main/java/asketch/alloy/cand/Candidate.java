package asketch.alloy.cand;

import java.io.Serializable;

/**
 * This class represents the concrete values of each hole.
 */
public class Candidate implements Serializable {

  protected String value;
  protected int cost;

  public Candidate(String value) {
    this(value, 1);
  }

  public Candidate(String value, int cost) {
    this.value = value;
    this.cost = cost;
  }

  public String getValue() {
    return value;
  }

  public int getCost() {
    return cost;
  }

  /**
   * This is used mainly for debugging purpose.
   */
  public String prettyString() {
    return getValue();
  }

  @Override
  public String toString() {
    return getValue();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Candidate) {
      Candidate that = (Candidate) obj;
      return getValue().equals(that.getValue());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getValue().hashCode();
  }
}
