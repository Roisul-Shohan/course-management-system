package com.cms.model;

import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class Course {

    private ObjectId _id;
    private String name;
    private ObjectId assignedTeacher;
    private List<ObjectId> students;
    private String courseCode;

    public Course() {
        this.students = new ArrayList<>();
        this.courseCode = null;
    }

    public Course(String name, ObjectId assignedTeacher, String courseCode) {
        this.name = name;
        this.assignedTeacher = assignedTeacher;
        this.students = new ArrayList<>();
        this.courseCode = courseCode;
    }

    public Course(String name, String courseCode) {
        this.name = name;
        this.assignedTeacher = null;
        this.students = new ArrayList<>();
        this.courseCode = courseCode;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getAssignedTeacher() {
        return assignedTeacher;
    }

    public void setAssignedTeacher(ObjectId assignedTeacher) {
        this.assignedTeacher = assignedTeacher;
    }

    public List<ObjectId> getStudents() {
        return students;
    }

    public void setStudents(List<ObjectId> students) {
        this.students = students;
    }

    public void addStudent(ObjectId studentId) {
        if (studentId != null && !students.contains(studentId)) {
            this.students.add(studentId);
        }
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
