package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations="classpath:application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradesDao;

    @Autowired
    private ScienceGradesDao scienceGradesDao;

    @Autowired
    private HistoryGradesDao historyGradesDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("insert into STUDENT(ID, FIRST_NAME, LAST_NAME, EMAIL_ADDRESS) values (1, 'Eric', 'Roby', 'eric.roby@luv2code_school.com')");
        jdbcTemplate.execute("insert into math_grade(student_id, grade) values (1, 90.05)");
        jdbcTemplate.execute("insert into science_grade(student_id, grade) values (1, 70.05)");
        jdbcTemplate.execute("insert into history_grade(student_id, grade) values (1, 80.55)");
    }

    @Test
    public void createStudentService() {
        studentService.createStudent("Chad", "Darby", "chad.darby@gmail.com");
        CollegeStudent student = studentDao.findByEmailAddress("chad.darby@gmail.com");
        assertEquals("chad.darby@gmail.com", student.getEmailAddress(), "find by email");
    }

    @Test
    public void isStudentNull() {
        CollegeStudent student = studentDao.findByEmailAddress("eric.roby@luv2code_school.com");
        assertTrue(studentService.checkIfStudentIsNull(student.getId()));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deletedStudent = Optional.ofNullable(studentDao.findByEmailAddress("eric.roby@luv2code_school.com"));
        assertTrue(deletedStudent.isPresent(), "student is present");
        studentService.deleteStudent(1);
        deletedStudent = Optional.ofNullable(studentDao.findById(1));
        assertFalse(deletedStudent.isPresent(), "student is not present");
    }

    @Sql("/insertData.sql")
    @Test
    public void getGradeBookService() {
        Iterable<CollegeStudent> iterableCollegeStudent = studentService.getGradeBook();

        List<CollegeStudent> studentList = new ArrayList<>();
        for (CollegeStudent student: iterableCollegeStudent){
            studentList.add(student);
        }
        assertEquals(5, studentList.size(), "student list size");
    }

    @Test
    public void createGradeService(){
        // create a grade for a student, takes in grade, student id, and course
        assertTrue(studentService.createGrade(80.50, 1, "math"));
        assertTrue(studentService.createGrade(90.50, 1, "science"));
        assertTrue(studentService.createGrade(79.50, 1, "history"));

        //Get all the grades with student id
        Iterable<MathGrade> mathGrades = mathGradesDao.findMathGradeByStudentId(1);
        Iterable<ScienceGrade>scienceGrades = scienceGradesDao.findScienceGradeByStudentId(1);
        Iterable<HistoryGrade>historyGrades = historyGradesDao.findHistoryGradeByStudentId(1);

        //verify there is a grade with the student id
        assertTrue(mathGrades.iterator().hasNext(), "Student has a math grade");
        assertTrue(scienceGrades.iterator().hasNext(), "Student has a science grade");
        assertTrue(historyGrades.iterator().hasNext(), "Student has a history grade");
    }

    @Test
    public void createGradeServiceReturnsFalse() {
        assertFalse(studentService.createGrade(180.50, 2, "math"));
        assertFalse(studentService.createGrade(-5, 2, "science"));
        assertFalse(studentService.createGrade(79.50, 5, "history")); //invalid student id
        assertFalse(studentService.createGrade(79.50, 2, "literature")); //invalid course
    }


    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("delete from student");
        jdbcTemplate.execute("delete from math_grade");
        jdbcTemplate.execute("delete from science_grade");
        jdbcTemplate.execute("delete from history_grade");
    }
}
