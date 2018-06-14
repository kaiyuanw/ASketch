pred test1 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node1
color = Node0->Blue0 + Node1->Red0
!undirected[]
}}}}
}
run test1
pred test2 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0
color = Node0->Blue0 + Node1->Red0
!undirected[]
}}}}
}
run test2
pred test3 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1, Node2: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1 + Node2
neighbors = Node0->Node1 + Node1->Node0 + Node1->Node1
color = Node0->Blue0 + Node1->Blue0 + Node2->Red0
!undirected[]
}}}}
}
run test3
pred test4 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
no neighbors
color = Node0->Blue0 + Node1->Red0
undirected[]
}}}}
}
run test4
pred test5 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0
no neighbors
color = Node0->Blue0
undirected[]
}}}}
}
run test5
pred test6 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node1 + Node1->Node0
color = Node0->Blue0 + Node1->Red0
undirected[]
}}}}
}
run test6
pred test7 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0
no neighbors
color = Node0->Red0
undirected[]
}}}}
}
run test7
pred test8 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node0->Node1
color = Node0->Blue0 + Node1->Red0
!graphIsConnected[]
}}}}
}
run test8
pred test9 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node1 + Node1->Node0
color = Node0->Blue0 + Node1->Red0
graphIsConnected[]
}}}}
}
run test9
pred test10 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node1
color = Node0->Blue0 + Node1->Red0
!graphIsConnected[]
}}}}
}
run test10
pred test11 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
no neighbors
color = Node0->Blue0 + Node1->Red0
!graphIsConnected[]
}}}}
}
run test11
pred test12 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0
no neighbors
color = Node0->Red0
graphIsConnected[]
}}}}
}
run test12
pred test13 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node1->Node0 + Node1->Node1
color = Node0->Blue0 + Node1->Red0
!graphIsConnected[]
}}}}
}
run test13
pred test14 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0
no neighbors
color = Node0->Blue0
graphIsConnected[]
}}}}
}
run test14
pred test15 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node0->Node1 + Node1->Node0
color = Node0->Blue0 + Node1->Red0
graphIsConnected[]
}}}}
}
run test15
pred test16 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node0->Node1 + Node1->Node1
color = Node0->Blue0 + Node1->Red0
!graphIsConnected[]
}}}}
}
run test16
pred test17 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node1->Node0
color = Node0->Blue0 + Node1->Red0
!graphIsConnected[]
}}}}
}
run test17
pred test18 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1, Node2: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1 + Node2
neighbors = Node0->Node1 + Node1->Node0 + Node1->Node2 + Node2->Node1
color = Node0->Blue0 + Node1->Blue0 + Node2->Red0
graphIsConnected[]
}}}}
}
run test18
pred test19 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node1 + Node1->Node0 + Node1->Node1
color = Node0->Blue0 + Node1->Red0
!treeAcyclic[]
}}}}
}
run test19
pred test20 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node1
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test20
pred test21 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node1->Node0
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test21
pred test22 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node0->Node1
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test22
pred test23 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node0->Node1 + Node1->Node0
color = Node0->Blue0 + Node1->Red0
!treeAcyclic[]
}}}}
}
run test23
pred test24 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node1 + Node1->Node0
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test24
pred test25 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1, Node2: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1 + Node2
neighbors = Node0->Node1 + Node0->Node2 + Node2->Node1 + Node2->Node2
color = Node0->Blue0 + Node1->Red0 + Node2->Red0
!treeAcyclic[]
}}}}
}
run test25
pred test26 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node1->Node0 + Node1->Node1
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test26
pred test27 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node1->Node1
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test27
pred test28 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node0 + Node0->Node1 + Node1->Node1
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test28
pred test29 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node0->Node1 + Node1->Node1
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test29
pred test30 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1, Node2: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1 + Node2
neighbors = Node0->Node1 + Node1->Node0 + Node2->Node1
color = Node0->Blue0 + Node1->Blue0 + Node2->Red0
treeAcyclic[]
}}}}
}
run test30
pred test31 {
some disj Red0: Red {some disj Blue0: Blue {some disj Red0, Blue0: Color {some disj Node0, Node1: Node {
Red = Red0
Blue = Blue0
Color = Red0 + Blue0
Node = Node0 + Node1
neighbors = Node1->Node0
color = Node0->Blue0 + Node1->Red0
treeAcyclic[]
}}}}
}
run test31
