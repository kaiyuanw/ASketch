package asketch.alloy.util;

import java.util.List;
import java.util.Map;

/**
 * This class represents a predicate invocation in the test valuation.
 */
public class PredCall {

  private String name;
  private List<String> parameters;
  private boolean isNegation;

  public PredCall(String name, List<String> parameters, boolean negation) {
    this.name = name;
    this.parameters = parameters;
    this.isNegation = negation;
  }

  public String getName() {
    return name;
  }

  public List<String> getParameters() {
    return parameters;
  }

  public boolean isNegation() {
    return isNegation;
  }

  public void updateParameters(Map<String, String> oldToNewNames) {
    for (int i = 0; i < parameters.size(); i++) {
      if (oldToNewNames.containsKey(parameters.get(i))) {
        parameters.set(i, oldToNewNames.get(parameters.get(i)));
      }
    }
  }

  @Override
  public String toString() {
    return "name: " + name + ", parameter list: " + parameters + ", isNegation: " + isNegation;
  }
}
