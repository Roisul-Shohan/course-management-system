package com.cms.dao;

import com.cms.config.DBconfig;
import com.cms.model.Student;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

public class StudentDAO {

    public MongoCollection<Document> studentCollection;

    public StudentDAO() {
        studentCollection = DBconfig.getDatabase().getCollection("students");
    }

    public void save(Student student) {
        Document doc = new Document()
                .append("fullname", student.getFullname())
                .append("username", student.getUsername())
                .append("email", student.getEmail())
                .append("password", student.getPassword())
                .append("courses", student.getCourses());
        studentCollection.insertOne(doc);
    }

    public void addCourseToStudent(ObjectId studentId, ObjectId courseId) {
        studentCollection.updateOne(
                Filters.eq("_id", studentId),
                Updates.addToSet("courses", courseId));
    }

    public Student findByUsername(String username) {
        Document doc = studentCollection.find(Filters.eq("username", username)).first();

        if (doc != null) {
            Student s = new Student();
            s.set_id(doc.getObjectId("_id"));
            s.setFullname(doc.getString("fullname"));
            s.setUsername(doc.getString("username"));
            s.setEmail(doc.getString("email"));
            s.setHashedPassword(doc.getString("password"));
            s.setCourses(doc.getList("courses", ObjectId.class));

            return s;
        }

        return null;
    }

    public Student findById(ObjectId studentId) {
        Document doc = studentCollection.find(Filters.eq("_id", studentId)).first();

        if (doc != null) {
            Student s = new Student();
            s.set_id(doc.getObjectId("_id"));
            s.setFullname(doc.getString("fullname"));
            s.setUsername(doc.getString("username"));
            s.setEmail(doc.getString("email"));
            s.setHashedPassword(doc.getString("password"));
            s.setCourses(doc.getList("courses", ObjectId.class));

            return s;
        }

        return null;
    }
}
