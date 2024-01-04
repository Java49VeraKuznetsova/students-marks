package telran.students;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import telran.exceptions.NotFoundException;
import telran.students.dto.Mark;
import telran.students.dto.Student;
import telran.students.service.StudentsService;
@SpringBootTest
class StudentsServiceTests {
	private static final String SERVICE_TEST = "Service: ";
	private static final long TEST_ID = 12345l;
	private static final String TEST_PHONE = "058-12345678";
	private static final Mark markTest = new Mark(DbTestCreation.SUBJECT_1, LocalDate.parse("2024-01-05"), 50 );
	
	@Autowired
StudentsService studentsService;
	@Autowired
	DbTestCreation dbCreation;
	@BeforeEach
	void setUp() {
		dbCreation.createDB();
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.GET_MARKS_NORMAL)
	void getMarksTest() {
		Mark[] marksActual = studentsService.getMarks(1).toArray(Mark[]::new);
		Mark[] marksExpected = dbCreation.getStudentMarks(1);
		assertArrayEquals(marksExpected, marksActual);
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.GET_MARKS_NOT_FOUND)
	void getMarksTestNotFoundTest() {
		assertThrowsExactly(NotFoundException.class,
				() -> studentsService.getMarks(TEST_ID));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.ADD_STUDENT_NORMAL)
	void addStudentTest() {
		Student studentExpected = new Student(TEST_ID, "Vasya", TEST_PHONE);
		assertEquals(studentExpected, studentsService.addStudent(studentExpected));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.ADD_STUDENT_ALREADY_EXISTS)
	void addStudentExistsStudentTest() {
		assertThrowsExactly(IllegalStateException.class,
				() -> studentsService.addStudent(dbCreation.getStudent(1)));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.UPDATE_PHONE_NORMAL)
	void updatePhoneStudentTest() {
		//new Student(ID_1, NAME_1, PHONE_1)
		Student studentActual = studentsService.updatePhone(dbCreation.ID_1, TEST_PHONE);
		assertEquals(TEST_PHONE, studentActual.phone());
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.UPDATE_PHONE_NOT_FOUND)
	void updatePhoneStudentNotFoundTest() {
			assertThrowsExactly(NotFoundException.class,
				()-> studentsService.updatePhone(TEST_ID, TEST_PHONE));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.ADD_MARK_NORMAL)
	void addMarkTest() {
		List<Mark> marksActual = studentsService.addMark(dbCreation.ID_1, markTest);
		Mark[] marksExpected = dbCreation.getStudentMarks(1);
		assertEquals(marksExpected.length+1, marksActual.size());
		assertTrue(marksActual.contains(markTest));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.ADD_MARK_STUDENT_NOT_FOUND)
	void addMarkNotFoundTest() {
		assertThrowsExactly(NotFoundException.class,
				() -> studentsService.addMark(TEST_ID, markTest));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REMOVE_STUDENT_NORMAL)
	void removeStudentTest() {
		Student removeStudentActual = studentsService.removeStudent(dbCreation.ID_1);
		Student removeExpected = dbCreation.getStudent(dbCreation.ID_1);
		assertEquals (removeStudentActual, removeExpected);
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REMOVE_STUDENT_NOT_FOUND)
	void removeStudentNotFoundTest() {
		assertThrowsExactly(NotFoundException.class,
				()-> studentsService.removeStudent(TEST_ID));
	}
	

}
