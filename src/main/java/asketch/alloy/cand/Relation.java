package asketch.alloy.cand;

import static asketch.alloy.etc.Operators.EMPTY;

import java.util.List;

/**
 * This class represents Alloy relations with any arity.
 */
public class Relation extends Candidate {

  /**
   * Arity of the relation.
   */
  private int arity;
  /**
   * Types of the relation, this should be a list of sigs.
   */
  private List<Type> types;
  /**
   * Contains identity relation;
   */
  private boolean hasIden;
  /**
   * Cardinality of each type in the relation
   */
  private List<String> cards;
  /**
   * Root operator.
   */
  private Candidate op;
  /**
   * Sub-relations
   */
  private List<Relation> subRelations;

  public Relation(String value, int arity) {
    this(value, 0, arity);
  }

  public Relation(String value, int cost, int arity) {
    this(value, cost, arity, null, false, null);
  }

  public Relation(String value, int arity, List<Type> types, List<String> cards) {
    this(value, 0, arity, types, false, cards);
  }

  public Relation(String value, int arity, List<Type> types, boolean hasIden, List<String> cards) {
    this(value, 0, arity, types, hasIden, cards);
  }

  public Relation(String value, int cost, int arity, List<Type> types, boolean hasIden,
      List<String> cards) {
    this(value, cost, arity, types, hasIden, cards, EMPTY, null);
  }

  public Relation(String value, int cost, int arity, List<Type> types, boolean hasIden,
      List<String> cards, Candidate op, List<Relation> subRelations) {
    super(value, cost);
    this.arity = arity;
    this.types = types;
    this.hasIden = hasIden;
    this.cards = cards;
    this.op = op;
    this.subRelations = subRelations;
  }

  public int getArity() {
    return arity;
  }

  public List<Type> getTypes() {
    return types;
  }

  public boolean hasIden() {
    return hasIden;
  }

  public List<String> getCards() {
    return cards;
  }

  public Candidate getOp() {
    return op;
  }

  public List<Relation> getSubRelations() {
    return subRelations;
  }

  @Override
  public String prettyString() {
    return "<value: " + getValue() + ", arity: " + arity + ", types: " + types.toString()
        + ", cards: " + cards.toString() + ">";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Relation) {
      Relation that = (Relation) obj;
      return getValue().equals(that.getValue());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getValue().hashCode();
  }
}
