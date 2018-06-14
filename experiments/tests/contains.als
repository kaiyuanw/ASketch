pred Test0{
  some disj List0 : List |  some disj Node0 : Node |  some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0
    no header
    no link
    !Contains[List0, Object2]
  }
}

pred Test1{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    header = List0 -> Node0
    link = Node0 -> Node1
    !Contains[List0,Object2]
  }
}

pred Test2{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    no header
    link = Node0 -> Node0 + Node1 -> Node2
    !Contains[List0,Object2]
  }
}

pred Test3{
  some disj List0 : List |  some disj Object0 : Object {
    List = List0
    no Node
    Object = Object0
    no elem
    no header
    no link
    !Contains[List0,Object0]
  }
}

pred Test4{
  some disj List0 : List |  some disj Node0, Node1 : Node |  some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1
    header = List0->Node0
    no link
    Contains[List0,Object0]
  }
}


pred Test5{
  some disj List0 : List |  some disj Node0, Node1 : Node |  some disj Object0, Object1, Object2 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1
    header = List0->Node0
    no link
    !Contains[List0,Object2]
  }
}

pred Test6{
  some disj List0 : List |  some disj Node0, Node1 : Node |  some disj Object0, Object1 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1
    elem = Node0->Object0 + Node1->Object1
    header = List0 -> Node0
    link = Node1 -> Node1
    Contains[List0,Object0]
  }
}


pred Test7{
  some disj List0 : List |  some disj Node0, Node1 : Node |  some disj Object0, Object1 : Object {
    List = List0
    Node = Node0 + Node1
    Object = Object0 + Object1
    elem = Node0->Object0 + Node1->Object1
    header = List0 -> Node1
    link = Node0 -> Node1
    !Contains[List0,Object0]
  }
}

pred Test8{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List  = List0
    Node  = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    header = List0 -> Node0
    link  = Node1 -> Node2
    Contains[List0,Object0]
  }
}

pred Test9{
  some disj List0 : List  |  some disj Object0, Object1, Object2 : Object {
    List  = List0
    Node  = none
    Object = Object0 + Object1 + Object2
    elem = none->none
    header = none->none
    link  = none->none
    !Contains[List0,Object0]
  }
}

pred Test10{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List  = List0
    Node = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    header = List0 -> Node0
    link  = Node0 -> Node1 + Node1 -> Node1
    Contains[List0,Object0]
  }
}

pred Test11{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List  = List0
    Node = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    header = List0 -> Node0
    link  = Node0 -> Node1 + Node1 -> Node1
    Contains[List0, Object1]
  }
}

pred Test12{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List  = List0
    Node = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    header = List0 -> Node0
    link  = Node0 -> Node1 + Node1 -> Node1
    !Contains[List0, Object2]
  }
}

pred Test13{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List  = List0
    Node  = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    header = List0 -> Node0
    link  = Node0 -> Node1 + Node1 -> Node2
    Contains[List0,Object0]
  }
}

pred Test14{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List  = List0
    Node  = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    header = List0 -> Node0
    link  = Node0 -> Node1 + Node1 -> Node2
    Contains[List0, Object1]
  }
}

pred Test15{
  some disj List0 : List |  some disj Node0, Node1, Node2 : Node |  some disj Object0, Object1, Object2 : Object {
    List  = List0
    Node  = Node0 + Node1 + Node2
    Object = Object0 + Object1 + Object2
    elem = Node0->Object0 + Node1->Object1 + Node2 -> Object2
    header = List0 -> Node0
    link  = Node0 -> Node1 + Node1 -> Node2
    Contains[List0, Object2]
  }
}
