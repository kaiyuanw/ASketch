one sig List {
  header: lone Node
}

sig Node {
  elem: lone Object,
  link: lone Node
}

sig Object {}

pred Contains(l: List, e: Object) {
  -- e in l.header.*link.elem
  \E,e1\ \CO,co1\ \E,e2\
}
