package asketch.alloy.etc;

import asketch.alloy.cand.Candidate;
import asketch.alloy.cand.operator.All;
import asketch.alloy.cand.operator.And;
import asketch.alloy.cand.operator.BiImply;
import asketch.alloy.cand.operator.CrossProduct;
import asketch.alloy.cand.operator.Difference;
import asketch.alloy.cand.operator.Empty;
import asketch.alloy.cand.operator.Equal;
import asketch.alloy.cand.operator.Imply;
import asketch.alloy.cand.operator.In;
import asketch.alloy.cand.operator.Intersect;
import asketch.alloy.cand.operator.Join;
import asketch.alloy.cand.operator.Lone;
import asketch.alloy.cand.operator.No;
import asketch.alloy.cand.operator.Not;
import asketch.alloy.cand.operator.NotEqual;
import asketch.alloy.cand.operator.NotIn;
import asketch.alloy.cand.operator.One;
import asketch.alloy.cand.operator.Or;
import asketch.alloy.cand.operator.ReflexiveTransitiveClosure;
import asketch.alloy.cand.operator.Set;
import asketch.alloy.cand.operator.Some;
import asketch.alloy.cand.operator.TransitiveClosure;
import asketch.alloy.cand.operator.Transpose;
import asketch.alloy.cand.operator.Union;

/**
 * This class includes all operators in Alloy.
 */
public class Operators {

  // Signature Quantifiers, Quantifiers, Unary Operators
  public static final Candidate EMPTY = new Empty();
  public static final Candidate NO = new No();
  public static final Candidate LONE = new Lone();
  public static final Candidate ONE = new One();
  public static final Candidate SOME = new Some();
  public static final Candidate ALL = new All();
  public static final Candidate SET = new Set();

  // Logical Operators
  public static final Candidate OR = new Or();
  public static final Candidate AND = new And();
  public static final Candidate BIIMPLY = new BiImply();
  public static final Candidate IMPLY = new Imply();

  // Compare Operators
  public static final Candidate EQ = new Equal();
  public static final Candidate NEQ = new NotEqual();
  public static final Candidate IN = new In();
  public static final Candidate NIN = new NotIn();

  // Unary Operators for formulas
  public static final Candidate NOT = new Not();
  // EMPTY is declared above.

  // Unary Operators for expressions
  public static final Candidate TILDE = new Transpose();
  public static final Candidate STAR = new ReflexiveTransitiveClosure();
  public static final Candidate CARET = new TransitiveClosure();

  // Binary Operators
  public static final Candidate AMP = new Intersect();
  public static final Candidate PLUS = new Union();
  public static final Candidate MINUS = new Difference();
  // Not used in BO
  public static final Candidate DOT = new Join();
  public static final Candidate ARROW = new CrossProduct();
}
