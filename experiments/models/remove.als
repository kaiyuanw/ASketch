one sig List {
  header, header': lone Node
}

sig Node {
  elem, elem': lone Object,
  link, link': lone Node
}

sig Object {}

pred Remove(l: List, e: Object) {
  -- l.header.*link.elem - e = l.header'.*link'.elem'
  \E,e1\ \BO,bo1\ \E,e2\ = \E,e1\
}