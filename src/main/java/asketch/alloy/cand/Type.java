package asketch.alloy.cand;

import java.io.Serializable;

public class Type implements Serializable {

  private String genType;
  private String pruneType;

  public Type(String genType, String pruneType) {
    this.genType = genType;
    this.pruneType = pruneType;
  }

  public String getGenType() {
    return genType;
  }

  public String getPruneType() {
    return pruneType;
  }

  @Override
  public String toString() {
    return genType;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Type) {
      Type that = (Type) obj;
      return getGenType().equals(that.getGenType());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getGenType().hashCode();
  }
}
