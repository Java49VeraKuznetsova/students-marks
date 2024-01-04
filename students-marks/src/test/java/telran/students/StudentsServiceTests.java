package telran.students;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import telran.exceptions.NotFoundException;
import telran.students.dto.Mark;
import telran.students.service.StudentsService;
@SpringBootTest
class StudentsServiceTests {
	private static final String SERVICE_TEST = "Service: ";
	private static final long WRONG_ID = 12345l;
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
				() -> studentsService.getMarks(WRONG_ID));
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.ADD_STUDENT_ALREADY_EXISTS)
	void addStudentExistsStudentTest() {
		assertThrowsExactly(IllegalStateException.class,
				() -> studentsService.addStudent(DbTestCreation.getStudent(1)));
	}
	
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.UPDATE_PHONE_NOT_FOUND)
	void updatePhoneStudentNotFoundTest() {
			assertThrowsExactly(NotFoundException.class,
				()-> studentsService.updatePhone(WRONG_ID, TEST_PHONE));
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.ADD_MARK_STUDENT_NOT_FOUND)
	void addMarkNotFoundTest() {
		assertThrowsExactly(NotFoundException.class,
				() -> studentsService.addMark(WRONG_ID, markTest));
	}
	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REMOVE_STUDENT_NOT_FOUND)
	void removeStudentNotFoundTest() {
		assertThrowsExactly(NotFoundException.class,
				()-> studentsService.removeStudent(WRONG_ID));
	}
	

}
