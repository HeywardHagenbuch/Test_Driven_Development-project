package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource("/application.properties")
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    JdbcTemplate template;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    StudentDao studentDao;

    @Mock
    private StudentAndGradeService studentAndGradeService;

    @BeforeAll // beforeAll methods must be static
    public static void setUpRequest() {
        request = new MockHttpServletRequest();
        request.setParameter("firstName", "Chad");
        request.setParameter("lastName", "Darby");
        request.setParameter("emailAddress", "chad.darby@luv2code_school.com");

    }

    @BeforeEach
    public void setUp() {
        template.execute("insert into STUDENT(FIRST_NAME, LAST_NAME, EMAIL_ADDRESS) values ('Eric', 'Roby', 'eric.roby@luv2code_school.com')");
    }

    @Test
    public void getStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("Eric", "Roby", "eric.roby@luv2code_school.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("Chad", "Darby", "chad.darby@luv2code_school.com");

        List<CollegeStudent> collegeStudents = new ArrayList<>(Arrays.asList(studentOne, studentTwo));
        when(studentAndGradeService.getGradeBook()).thenReturn(collegeStudents);
        assertIterableEquals(collegeStudents, studentAndGradeService.getGradeBook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
    }


    @Test
    public void createStudentHttpRequest() throws Exception {

        CollegeStudent collegeStudentOne = new GradebookCollegeStudent("Eric", "Roby", "eric.roby@luv2code_school.com");
        List<CollegeStudent> studentList = new ArrayList<>(Arrays.asList(collegeStudentOne));
        when(studentAndGradeService.getGradeBook()).thenReturn(studentList);

        assertIterableEquals(studentList, studentAndGradeService.getGradeBook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/").contentType(MediaType.APPLICATION_JSON)
                .param("firstName", request.getParameterValues("firstName"))
                .param("lastName", request.getParameterValues("lastName"))
                .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");
        assertNotNull(verifyStudent);
    }

    @Test
    public void deleteStudent() throws Exception {

        assertThat(studentDao.findById(1)).isNotNull();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{1}", 1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
        assertThat(studentDao.findById(1)).isNull();
    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception{
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{1}", 0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @AfterEach
    public void tearDown() {
        template.execute("delete from STUDENT where EMAIL_ADDRESS = 'eric.roby@luv2code_school.com'");
    }
}
