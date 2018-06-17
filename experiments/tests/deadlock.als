pred Test0{
  some disj State0, State1, State2 : State {
    Process = none
    State = State0 + State1 + State2
    Mutex = none
    holds = none -> none -> none
    waits = none -> none -> none
    !Deadlock[]
  }
}

pred Test1{
  some disj Mutex0, Mutex1, Mutex2 : Mutex |  some disj State0, State1, State2 : State {
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    no Process
    no holds
    no waits
    !Deadlock[]
  }
}

pred Test2{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1 : Mutex {
    Process = none
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1
    holds = none -> none -> none
    waits = none -> none -> none
    !Deadlock[]
  }
}

pred Test3{
  some disj Process0 : Process |  some disj Mutex0 : Mutex |  some disj State0, State1, State2: State {
    Process = Process0
    Mutex = Mutex0
    State = State0 + State1 + State2
    holds = State0->Process0->Mutex0 + State1->Process0->Mutex0
    waits = State0->Process0->Mutex0
    Deadlock[]
  }
}

pred Test4{
  some disj Process0 : Process |  some disj Mutex0, Mutex1, Mutex2 : Mutex |  some disj State0, State1, State2 : State {
    Process = Process0
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    waits = State0->Process0->Mutex0
    no holds
    Deadlock[]
  }
}

pred Test5{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0 : Process {
    Process = Process0
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    waits = none -> none -> none
    holds = State0->Process0->Mutex2 + State1->Process0->Mutex2 + State2->Process0->Mutex2
    !Deadlock[]
  }
}

pred Test6{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1 : Process {
    Process = Process0 + Process1
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    holds = none -> none -> none
    waits = State0->Process0->Mutex2 + State0->Process1->Mutex0 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State1->Process0->Mutex1 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process1->Mutex0 + State2->Process1->Mutex1
    Deadlock[]
  }
}

pred Test7{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1 : Process {
    Process = Process0 + Process1
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    holds = State0->Process0->Mutex2 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process1->Mutex0 + State2->Process1->Mutex1 + State2->Process1->Mutex2
    waits = State0->Process1->Mutex2 + State1->Process0->Mutex2 + State1->Process1->Mutex2 + State2->Process1->Mutex2
    Deadlock[]
  }
}

pred Test8{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1, Process2 : Process {
    Process = Process0 + Process1 + Process2
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    holds = State0->Process0->Mutex2 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State0->Process2->Mutex1 + State0->Process2->Mutex2 + State1->Process2->Mutex0 + State1->Process2->Mutex1
    waits = State0->Process0->Mutex2 + State0->Process1->Mutex1 + State1->Process0->Mutex2 + State1->Process2->Mutex1 + State2->Process1->Mutex1 + State2->Process2->Mutex0 + State2->Process2->Mutex2
    !Deadlock[]
  }
}

pred Test9{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1 : Process {
    Process = Process0 + Process1
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    holds = State0->Process0->Mutex2 + State0->Process1->Mutex1 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process1->Mutex0 + State2->Process1->Mutex1 + State2->Process1->Mutex2
    waits = State0->Process1->Mutex0 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process0->Mutex0 + State2->Process0->Mutex1
    !Deadlock[]
  }
}

pred Test10{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0, Process1 : Process {
    Process = Process0 + Process1
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    holds = State0->Process0->Mutex2 + State0->Process1->Mutex0 + State0->Process1->Mutex1 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process1->Mutex0 + State2->Process1->Mutex1 + State2->Process1->Mutex2
    waits = State0->Process1->Mutex0 + State0->Process1->Mutex1 + State0->Process1->Mutex2 + State1->Process1->Mutex0 + State1->Process1->Mutex1 + State1->Process1->Mutex2 + State2->Process1->Mutex0 + State2->Process1->Mutex1
    !Deadlock[]
  }
}

pred Test11{
  some disj State0, State1, State2 : State | some disj Mutex0, Mutex1, Mutex2 : Mutex | some disj Process0 : Process {
    Process = Process0
    State = State0 + State1 + State2
    Mutex = Mutex0 + Mutex1 + Mutex2
    holds =State0->Process0->Mutex0 + State0->Process0->Mutex1 + State0->Process0->Mutex2 + State1->Process0->Mutex0 + State1->Process0->Mutex1 + State1->Process0->Mutex2 + State2->Process0->Mutex0 + State2->Process0->Mutex1 + State2->Process0->Mutex2
    waits = State0->Process0->Mutex0 + State0->Process0->Mutex1 + State0->Process0->Mutex2 + State1->Process0->Mutex0 + State1->Process0->Mutex1 + State1->Process0->Mutex2 + State2->Process0->Mutex0 + State2->Process0->Mutex1 + State2->Process0->Mutex2
    Deadlock[]
  }
}

pred Test12{
  some disj Process0 : Process |  some disj Mutex0, Mutex1, Mutex2 : Mutex |  some disj State0, State1, State2 : State {
    Process = Process0
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    holds = State0->Process0->Mutex0 + State0->Process0->Mutex1 + State0->Process0->Mutex2 + State1->Process0->Mutex0 + State1->Process0->Mutex1 + State1->Process0->Mutex2 + State2->Process0->Mutex0 + State2->Process0->Mutex1 + State2->Process0->Mutex2
    no waits
    !Deadlock[]
  }
}

pred Test13{
  some disj Process0 : Process |  some disj Mutex0, Mutex1, Mutex2 : Mutex |  some disj State0, State1, State2 : State {
    Process = Process0
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    holds = State0->Process0->Mutex0 + State0->Process0->Mutex1 + State0->Process0->Mutex2 + State1->Process0->Mutex0 + State1->Process0->Mutex1 + State1->Process0->Mutex2 + State2->Process0->Mutex0 + State2->Process0->Mutex1 + State2->Process0->Mutex2
    waits = State0->Process0->Mutex0 + State0->Process0->Mutex1 + State0->Process0->Mutex2
    Deadlock[]
  }
}

pred Test14{
  some disj Process0, Process1, Process2 : Process |  some disj Mutex0, Mutex1, Mutex2: Mutex |  some disj State0, State1, State2 : State {
    Process = Process0 + Process1 + Process2
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    holds = State0->Process2->Mutex0 + State0->Process2->Mutex1 + State0->Process2->Mutex2 + State1->Process2->Mutex0 + State1->Process2->Mutex1 + State1->Process2->Mutex2 + State2->Process2->Mutex0 + State2->Process2->Mutex1 + State2->Process2->Mutex2
    no waits
    !Deadlock[]
  }
}

pred Test15{
  some disj Process0, Process1 : Process |  some disj Mutex0, Mutex1, Mutex2 : Mutex |  some disj State0, State1, State2 : State {
    Process = Process0 + Process1
    Mutex = Mutex0 + Mutex1 + Mutex2
    State = State0 + State1 + State2
    holds = State2->Process0->Mutex0 + State2->Process0->Mutex1 + State2->Process0->Mutex2
    waits = State0->Process0->Mutex1 + State1->Process0->Mutex0 + State1->Process1->Mutex2
    Deadlock[]
  }
}
