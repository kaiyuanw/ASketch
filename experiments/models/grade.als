abstract sig Person {}

sig Student extends Person {}

sig Professor extends Person {}

sig Class {
  assistant_for: set Student,
  instructor_of: one Professor
}

sig Assignment {
  associated_with: one Class,
  assigned_to: some Student
}

pred PolicyAllowsGrading(s: Person, a: Assignment) {
  -- s in a.associated_with.assistant_for || s in a.associated_with.instructor_of
  \E,e\ \CO,co\ \E,e\ \LO,lo\ \E,e\ \CO,co\ \E,e\
  -- s !in a.assigned_to
  \E,e\ \CO,co\ \E,e\
}