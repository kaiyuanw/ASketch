pred test1 {
  some disj Element0: Element {some disj Array0: Array {
    Element = Element0
    Array = Array0
    i2e = Array0->0->Element0 + Array0->1->Element0
    length = Array0->3
    NoConflict[]
  }}
}
run test1

pred test2 {
  some disj Element0, Element1, Element2: Element {some disj Array0: Array {
    Element = Element0 + Element1 + Element2
    Array = Array0
    i2e = Array0->0->Element0 + Array0->0->Element1 + Array0->0->Element2
    length = Array0->5
    !NoConflict[]
}}
}
run test2

pred test3 {
  some disj Element0: Element {some disj Array0: Array {
    Element = Element0
    Array = Array0
    i2e = Array0->1->Element0
    length = Array0->5
    NoConflict[]
  }}
}
run test3

pred test4 {
  some disj Array0: Array {
    no Element
    Array = Array0
    no i2e
    length = Array0->0
    NoConflict[]
  }
}
run test4

pred test5 {
  some disj Element0, Element1: Element {some disj Array0: Array {
    Element = Element0 + Element1
    Array = Array0
    i2e = Array0->0->Element0 + Array0->0->Element1
    length = Array0->4
    !NoConflict[]
  }}
}
run test5