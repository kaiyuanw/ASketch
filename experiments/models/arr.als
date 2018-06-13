sig Element {}

one sig Array {
  // Maps indexes to elements of Element.
  i2e: Int -> Element,
  // Represents the length of the array.
  length: Int
}

pred NoConflict() {
  // Each index maps to at most one element.
  -- all idx: Array.i2e.Element | lone Array.i2e[idx]
  \Q,q\ idx: Array.i2e.Element | \UO,uo\ \E,e\
}

run NoConflict for 3
