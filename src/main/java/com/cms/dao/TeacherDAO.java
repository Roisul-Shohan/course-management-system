package com.cms.dao;

import com.cms.config.DBconfig;
import com.cms.model.Teacher;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

public class TeacherDAO {

    public MongoCollection<Document> teacherCollection;

    public TeacherDAO() {
        teacherCollection = DBconfig.getDatabase().getCollection("teachers");
    }

    public void save(Teacher teacher) {
        Document doc = new Document()
                .append("fullname", teacher.getFullname())
                .append("username", teacher.getUsername())
                .append("email", teacher.getEmail())
                .append("password", teacher.getPassword())
                .append("courses", teacher.getCourses());
        teacherCollection.insertOne(doc);
    }

    public Teacher findByUsername(String username) {
        Document doc = teacherCollection.find(Filters.eq("username", username)).first();
        if (doc != null) {
            Teacher t = new Teacher();
            t.set_id(doc.getObjectId("_id"));
            t.setFullname(doc.getString("fullname"));
            t.setUsername(doc.getString("username"));
            t.setEmail(doc.getString("email"));
            t.setHashedPassword(doc.getString("password"));
            t.setCourses(doc.getList("courses", ObjectId.class));
            return t;
        }
        return null;
    }

    public void addCourseToTeacher(ObjectId teacherId, ObjectId courseId) {
        teacherCollection.updateOne(
                Filters.eq("_id", teacherId),
                Updates.addToSet("courses", courseId));
    }

    public Teacher findById(ObjectId teacherId) {
        Document doc = teacherCollection.find(Filters.eq("_id", teacherId)).first();
        if (doc != null) {
            Teacher t = new Teacher();
            t.set_id(doc.getObjectId("_id"));
            t.setFullname(doc.getString("fullname"));
            t.setUsername(doc.getString("username"));
            t.setEmail(doc.getString("email"));
            t.setHashedPassword(doc.getString("password"));
            t.setCourses(doc.getList("courses", ObjectId.class));
            return t;
        }
        return null;
    }

    public void updateTeacher(Teacher teacher) {
        Document updateDoc = new Document()
                .append("fullname", teacher.getFullname())
                .append("username", teacher.getUsername())
                .append("email", teacher.getEmail())
                .append("password", teacher.getPassword())
                .append("courses", teacher.getCourses());
            
        teacherCollection.updateOne(
                Filters.eq("_id", teacher.get_id()),
                new Document("$set", updateDoc));
    }

}
