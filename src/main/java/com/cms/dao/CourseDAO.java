package com.cms.dao;

import com.cms.config.DBconfig;
import com.cms.model.Course;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public MongoCollection<Document> courseCollection;

    public CourseDAO() {
        courseCollection = DBconfig.getDatabase().getCollection("courses");
    }

    public void save(Course course) {
        Document doc = new Document()
                .append("name", course.getName())
                .append("assigned_teacher", course.getAssignedTeacher())
                .append("students", course.getStudents())
                .append("courseCode", course.getCourseCode());
        courseCollection.insertOne(doc);
    }

    public Course findById(ObjectId courseId) {
        Document doc = courseCollection.find(Filters.eq("_id", courseId)).first();
        if (doc != null) {
            return mapDocumentToCourse(doc);
        }
        return null;
    }

    public Course findByName(String name) {
        Document doc = courseCollection.find(Filters.eq("name", name)).first();
        if (doc != null) {
            return mapDocumentToCourse(doc);
        }
        return null;
    }

    public void assignTeacher(ObjectId courseId, ObjectId teacherId) {
        courseCollection.updateOne(
                Filters.eq("_id", courseId),
                Updates.set("assigned_teacher", teacherId));
    }

    public void assignTeacherToCourse(ObjectId courseId, ObjectId teacherId) {
        courseCollection.updateOne(
                Filters.eq("_id", courseId),
                Updates.set("assigned_teacher", teacherId));
    }

    public void addStudentToCourse(ObjectId courseId, ObjectId studentId) {
        courseCollection.updateOne(
                Filters.eq("_id", courseId),
                Updates.addToSet("students", studentId));
    }

    private Course mapDocumentToCourse(Document doc) {
        Course c = new Course();
        c.set_id(doc.getObjectId("_id"));
        c.setName(doc.getString("name"));
        c.setAssignedTeacher(doc.getObjectId("assigned_teacher"));
        List<ObjectId> studentList = doc.getList("students", ObjectId.class);
        c.setStudents(studentList);
        c.setCourseCode(doc.getString("courseCode")); // Add this
        return c;
    }

    public Course findByCourseCode(String courseCode) {
        Document doc = courseCollection.find(Filters.eq("courseCode", courseCode)).first();
        if (doc != null) {
            return mapDocumentToCourse(doc);
        }
        return null;
    }

    public List<Course> getAll() {
        List<Course> courses = new ArrayList<>();
        for (Document doc : courseCollection.find()) {
            courses.add(mapDocumentToCourse(doc));
        }
        return courses;
    }
}
