package telran.students.service;

import java.util.List;

import telran.students.dto.*;

public interface StudentsService {
Student addStudent(Student student);
Student updatePhone(long id, String phone);
List<Mark> addMark(long id, Mark mark);
Student removeStudent(long id);
List<Mark> getMarks(long id);
Student getStudentByPhone(String phoneNumber);
List<Student> getStudentsByPhonePhonePrefix(String phonePrefix);
List<Student> getStudentsAllGoodMarks (int threasholdScore);
List<Student> getStudentFewMarks(int thresholdNMarks);
//HW #74
/************************************************************************************/
//getting students who have at least one score of a given subject and all scores of that subject
//greater than or equal a given threshold
List<Student> getStudentsAllGoodMarksSubject(String subject, int thresholdScore);
/*********************************************************************************/
//getting students having number of marks in a closed range of the given values
//nMarks >= min && nMarks <= max
List<Student> getStudentsMarksAmountBetween(int min, int max);
//CW #75
List<Mark> getStudentSubjectMarks(long id, String subject);
}
