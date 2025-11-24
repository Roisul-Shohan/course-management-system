package com.cms.model;

import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.List;

public class Student {

    private ObjectId _id;
    private String fullname;
    private String username;
    private String email;
    private String password;
    private List<ObjectId> courses;

    public Student() {
        this.courses = new ArrayList<>();
    }

    public Student(String fullname, String username, String email, String plainPassword) {
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = hashPassword(plainPassword);
        this.courses = new ArrayList<>();
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    // Set password with hashing
    public void setPassword(String plainPassword) {
        this.password = hashPassword(plainPassword);
    }

    // Set already hashed password (for loading from DB)
    public void setHashedPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    public List<ObjectId> getCourses() {
        return courses;
    }

    public void setCourses(List<ObjectId> courses) {
        this.courses = courses;
    }

    public void addCourse(ObjectId courseId) {
        this.courses.add(courseId);
    }

}
