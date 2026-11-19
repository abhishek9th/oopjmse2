
package com.student.service;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentDAO dao;

    public StudentService(StudentDAO dao) {
        this.dao = dao;
    }

    // Validate scores
    private void validateScores(Student s) {

        if (s.getProgrammingScore() < 0 ||
                s.getProgrammingScore() > 100 ||
                s.getMathematicsScore() < 0 ||
                s.getMathematicsScore() > 100) {

            throw new IllegalArgumentException(
                    "Scores must be between 0 and 100"
            );
        }
    }

    // 1. Add student
    @Transactional
    public Student addStudent(Student s) {

        validateScores(s);

        dao.addStudent(s);

        return s;
    }

    // 2. Get all students
    public List<Student> getAllStudents() {
        return dao.getAllStudents();
    }

    // 3. Search by ID
    public Student getStudentById(int id) {

        Student s = dao.getStudentById(id);

        if (s == null) {
            throw new RuntimeException("Student not found");
        }

        return s;
    }

    // 4. Update scores and skills
    @Transactional
    public Student updateStudent(int id, Student s) {

        validateScores(s);

        Student existing = getStudentById(id);

        int result = dao.updateStudent(id, s);

        if (result == 0) {
            throw new RuntimeException("Student update failed");
        }

        return dao.getStudentById(id);
    }

    // 5. Delete student
    public void deleteStudent(int id) {

        int result = dao.deleteStudent(id);

        if (result == 0) {
            throw new RuntimeException("Student not found");
        }
    }

    // 6. Merit list
    public List<Student> getMeritList() {
        return dao.getMeritList();
    }
}
