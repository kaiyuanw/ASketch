abstract sig Color {}
one sig Red extends Color {}
one sig Blue extends Color {}

sig Node {
  neighbors: set Node,
  color: one Color 
} 	

pred undirected {
  -- neighbors = ~neighbors
  \E,e1\ \CO,co\ \E,e1\
  -- no iden & neighbors
  \UO,uo\ \E,e1\
}

pred graphIsConnected {
  all n1: Node | all n2: Node-n1 |
    -- n1 in n2.^neighbors
    \E,e2\ \CO,co\ \E,e2\
}

pred treeAcyclic {
  all n1, n2: Node |
    -- n1 in n2.neighbors => n1 !in n2.^(neighbors-(n2->n1))
    \E,e2\ \CO,co\ \E,e2\ \LO,lo\ \E,e2\ \CO,co\ \E,e2\
} 

run {} for 3 Node
