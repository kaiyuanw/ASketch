module bt

one sig BinaryTree {
  root: lone Node
}

sig Node {
  left, right: lone Node
}

pred IsTree() {
  all n: Node {
    n in BinaryTree.root.*(left + right) => {
      n \CO,co1\ \E,e1\ -- n !in n.^(left + right)
      \UO,uo1\ \E,e2\ -- no n.left & n.right
      \UO,uo1\ \E,e3\ -- lone n.~(left + right)
    }
  }
}
