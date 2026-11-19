
package com.student.dao;

import com.student.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentDAO {

    private final JdbcTemplate jdbcTemplate;

    public StudentDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get skills of a student
    private List<String> getSkills(int studentId) {

        String sql = "SELECT skill_name FROM StudentSkills WHERE student_id = ?";

        return jdbcTemplate.queryForList(
                sql, String.class, studentId
        );
    }

    // Add student
    public int addStudent(Student s) {

        String sql = "INSERT INTO Student VALUES (?, ?, ?, ?, ?, ?)";

        int result = jdbcTemplate.update(
                sql,
                s.getStudentId(),
                s.getStudentName(),
                s.getEmail(),
                s.getCourseName(),
                s.getProgrammingScore(),
                s.getMathematicsScore()
        );

        // Insert technical skills
        String skillSQL =
                "INSERT INTO StudentSkills(student_id, skill_name) VALUES (?, ?)";

        for (String skill : s.getTechnicalSkills()) {
            jdbcTemplate.update(
                    skillSQL,
                    s.getStudentId(),
                    skill
            );
        }

        return result;
    }

    // Retrieve all students
    public List<Student> getAllStudents() {

        String sql = "SELECT * FROM Student";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            int id = rs.getInt("student_id");

            return new Student(
                    id,
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("course_name"),
                    rs.getDouble("programming_score"),
                    rs.getDouble("mathematics_score"),
                    getSkills(id)
            );
        });
    }

    // Search student by ID
    public Student getStudentById(int id) {

        String sql = "SELECT * FROM Student WHERE student_id = ?";

        List<Student> students = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Student(
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getString("email"),
                        rs.getString("course_name"),
                        rs.getDouble("programming_score"),
                        rs.getDouble("mathematics_score"),
                        getSkills(rs.getInt("student_id"))
                ),
                id
        );

        return students.isEmpty() ? null : students.get(0);
    }

    // Update student scores
    public int updateStudent(int id, Student s) {

        String sql = """
                UPDATE Student
                SET programming_score = ?,
                    mathematics_score = ?
                WHERE student_id = ?
                """;

        int result = jdbcTemplate.update(
                sql,
                s.getProgrammingScore(),
                s.getMathematicsScore(),
                id
        );

        if (result > 0) {

            // Remove old skills
            jdbcTemplate.update(
                    "DELETE FROM StudentSkills WHERE student_id = ?",
                    id
            );

            // Insert new skills
            String skillSQL =
                    "INSERT INTO StudentSkills(student_id, skill_name) VALUES (?, ?)";

            for (String skill : s.getTechnicalSkills()) {
                jdbcTemplate.update(
                        skillSQL,
                        id,
                        skill
                );
            }
        }

        return result;
    }

    // Delete student
    public int deleteStudent(int id) {

        return jdbcTemplate.update(
                "DELETE FROM Student WHERE student_id = ?",
                id
        );
    }

    // Merit list
    public List<Student> getMeritList() {

        String sql = """
                SELECT * FROM Student
                ORDER BY (programming_score + mathematics_score) / 2 DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            int id = rs.getInt("student_id");

            return new Student(
                    id,
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("course_name"),
                    rs.getDouble("programming_score"),
                    rs.getDouble("mathematics_score"),
                    getSkills(id)
            );
        });
    }
}
