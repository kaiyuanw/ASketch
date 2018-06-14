pred test1 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
no ext
Acyclic[]
}}
}
run test1

pred test2 {
some disj Object0: Object {some disj Object0: Class {
Object = Object0
Class = Object0
no ext
Acyclic[]
}}
}
run test2

pred test3 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Object0->Object0 + Class0->Class0
!AllExtObject[]
}}
}
run test3

pred test4 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Object0->Object0 + Class0->Class0
!Acyclic[]
}}
}
run test4

pred test5 {
some disj Object0: Object {some disj Object0, Class0, Class1: Class {
Object = Object0
Class = Object0 + Class0 + Class1
ext = Class0->Class1 + Class1->Object0
AllExtObject[]
}}
}
run test5

pred test6 {
some disj Object0: Object {some disj Object0, Class0, Class1: Class {
Object = Object0
Class = Object0 + Class0 + Class1
ext = Object0->Class1 + Class0->Class1 + Class1->Class0
!Acyclic[]
}}
}
run test6

pred test7 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Object0->Class0 + Class0->Class0
!AllExtObject[]
}}
}
run test7

pred test8 {
some disj Object0: Object {some disj Object0: Class {
Object = Object0
Class = Object0
no ext
AllExtObject[]
}}
}
run test8

pred test9 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
no ext
!ClassHierarchy[]
}}
}
run test9

pred test10 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Object0->Class0 + Class0->Class0
!ObjectNoExt[]
}}
}
run test10

pred test11 {
some disj Object0: Object {some disj Object0, Class0, Class1: Class {
Object = Object0
Class = Object0 + Class0 + Class1
ext = Class0->Object0 + Class1->Object0
AllExtObject[]
}}
}
run test11

pred test12 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Class0->Class0
ObjectNoExt[]
}}
}
run test12

pred test13 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Object0->Object0 + Class0->Object0
AllExtObject[]
}}
}
run test13

pred test14 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Object0->Class0 + Class0->Class0
!ClassHierarchy[]
}}
}
run test14

pred test15 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Object0->Class0 + Class0->Object0
!Acyclic[]
}}
}
run test15

pred test16 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Class0->Class0
!ClassHierarchy[]
}}
}
run test16

pred test17 {
some disj Object0: Object {some disj Object0, Class0: Class {
Object = Object0
Class = Object0 + Class0
ext = Object0->Class0 + Class0->Class0
!Acyclic[]
}}
}
run test17