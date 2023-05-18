package com.luv2code.springmvc.models;

public class GradebookCollegeStudent extends CollegeStudent{

    private int id;

    private StudentGrades studentGrades;

    public GradebookCollegeStudent(String firstName, String lastName, String emailAddress) {
        super(firstName, lastName, emailAddress);
    }

    public GradebookCollegeStudent(int id, String firstName, String lastName, String emailAddress, StudentGrades studentGrades) {
        super(firstName, lastName, emailAddress);
        this.studentGrades = studentGrades;
        this.id = id;
    }

    public StudentGrades getStudentGrades() {
        return studentGrades;
    }

    public void setStudentGrades(StudentGrades studentGrades) {
        this.studentGrades = studentGrades;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
