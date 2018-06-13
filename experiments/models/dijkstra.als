module dijkstra

open util/ordering [State] as so

sig Process {}
sig Mutex {}

sig State {
  holds, waits: Process -> Mutex
} -- due to ordering, tests do not define State sig valuation

pred Deadlock() {
  -- some Process
  \UO,uo1\ \E,e1\
  -- some s: State | all p: Process | some p.(s.waits)
  \Q,q1\ s: State | \Q,q1\ p: Process | \UO,uo1\ \E,e2\
}
