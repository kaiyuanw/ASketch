one sig List {
  header: lone Node
}

sig Node {
  link: lone Node
}

pred Acyclic() {
--  all n: Node | n in List.header.*link => n !in n.^link
  \Q,q\ n: Node | n \CO,co\ \E,e\ => n \CO,co\ \E,e\
}