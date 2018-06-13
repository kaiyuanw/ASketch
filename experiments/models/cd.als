sig Class {
  ext: lone Class
}

one sig Object extends Class {}

pred ObjectNoExt() {
  // Object does not extend any class.
  -- no Object.ext
  \UO,uo\ \E,e1\
}

pred Acyclic() {
  // No class is a sub-class of itself (transitively).
  -- all c: Class | c !in c.^ext
  \Q,q\ c: Class | \E,e2\ \CO,co\ \E,e2\
}

pred AllExtObject() {
  // Each class other than Object is a sub-class of Object.
  -- all c: Class - Object | c in Object.^~ext
  \Q,q\ c: Class - Object | \E,e2\ \CO,co\ \E,e2\
}

pred ClassHierarchy() {
  ObjectNoExt
  Acyclic
  AllExtObject
}

run ClassHierarchy for 3
