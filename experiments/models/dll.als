one sig DLL {
  header: lone Node
}

sig Node {
  pre, nxt: lone Node,
  elem: Int
}

pred UniqueElem() {
  // Unique nodes contain unique elements.
  -- no disj n1, n2: Node | n1.elem = n2.elem
  \Q,q\ disj n1, n2: Node | \E,e1\ \CO,co\ \E,e1\
}

pred Sorted() {
  // The list is sorted in ascending order (<=) along nxt.
  all n: Node | some n.nxt => n.elem <= n.nxt.elem
  \Q,q\ n: Node | \UO,uo\ \E,e2\ \LO,lo\ \E,e3\ <= \E,e3\
}

pred ConsistentPreAndNxt() {
  // For any node n1 and n2, if n1.nxt = n2, then n2.pre = n1; and vice versa.
  nxt = ~pre
  \E,e4\ \CO,co\ \E,e4\
}