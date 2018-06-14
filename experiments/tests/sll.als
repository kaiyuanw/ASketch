pred Test0{
  some List0: List {
    List = List0
    no Node
    header = none -> none
    link = none -> none
    Acyclic[]
  }
}

pred Test1{
  some List0: List | some Node0: Node {
    List = List0
    Node = Node0
    header = List0 -> Node0
    link = Node0 -> Node0
    !Acyclic[]
  }
}

pred Test2{
  some List0: List | some disj Node0, Node1 : Node {
    List = List0
    Node = Node0 + Node1
    header = List0 -> Node0
    link = Node0 -> Node1 + Node1 -> Node0
    !Acyclic[]
  }
}

pred Test3{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0 -> Node0
    link = Node0 -> Node1 + Node2 -> Node2
    Acyclic[]
  }
}

pred Test4{
  some List0: List | some disj Node0, Node1, Node2: Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0 -> Node0
    link = Node0 -> Node1 + Node1 -> Node2 + Node2 -> Node2
    !Acyclic[]
  }
}

pred Test5{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0 -> Node2
    link = Node1 -> Node0 + Node2 -> Node1
    Acyclic[]
  }
}

pred Test6{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0 -> Node2
    link = Node0 -> Node2 + Node1 -> Node0 + Node2 -> Node1
    !Acyclic[]
  }
}

pred Test7{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0 -> Node2
    link = Node0 -> Node1 + Node1 -> Node0 + Node2 -> Node1
    !Acyclic[]
  }
}

pred Test8{
  some List0: List | some disj Node0 : Node {
    List = List0
    Node = Node0
    header = List0 -> Node0
    link = Node0 -> Node0
    !Acyclic[]
  }
}

pred Test9{
  some List0: List | some disj Node0, Node1 : Node {
    List = List0
    Node = Node0 + Node1
    header = List0 -> Node0
    link = Node0 -> Node1
    Acyclic[]
  }
}

pred Test10{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    no header
    no link
    Acyclic[]
  }
}

pred Test11{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0->Node1
    link = Node0->Node1 + Node1->Node2
    Acyclic[]
  }
}

pred Test12{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0->Node1
    link = Node0->Node1 + Node1->Node0
    !Acyclic[]
  }
}

pred Test13{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0->Node1
    link = Node0->Node1 + Node2->Node1
    Acyclic[]
  }
}

pred Test14{
  some List0: List | some disj Node0, Node1, Node2 : Node {
    List = List0
    Node = Node0 + Node1 + Node2
    header = List0->Node0
    link = Node0->Node1 + Node2->Node1
    Acyclic[]
  }
}

pred Test15{
  some List0: List | some disj Node0, Node1 : Node {
    List = List0
    Node = Node0 + Node1
    no header
    link = Node0->Node0 + Node1->Node1
    Acyclic[]
  }
}
