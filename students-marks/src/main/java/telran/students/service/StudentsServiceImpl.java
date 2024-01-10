package telran.students.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.exceptions.NotFoundException;
import telran.students.dto.IdName;
import telran.students.dto.IdNamePhone;
import telran.students.dto.Mark;
import telran.students.dto.MarksOnly;
import telran.students.dto.Student;
import telran.students.model.StudentDoc;
import telran.students.repo.StudentRepo;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentsServiceImpl implements StudentsService {
final StudentRepo studentRepo;

	@Override
	@Transactional
	public Student addStudent(Student student) {
		long id = student.id();
		if(studentRepo.existsById(id)) {
			throw new IllegalStateException(String.format("Studentd %d already exists", id));
		}
		studentRepo.save(StudentDoc.of(student));
		log.debug("saved {}", student);
		return student;
	}

	@Override
	@Transactional
	public Student updatePhone(long id, String phone) {
	 StudentDoc studentDoc = getStudent(id);
	 studentDoc.setPhone(phone);
	 String oldPhone = studentDoc.getPhone();
	 studentRepo.save(studentDoc);
	 log.debug("student {}, old phone number {}, new phone number {}", id, oldPhone, phone);
	  return studentDoc.build();
	}

	private StudentDoc getStudent(long id) {
		return studentRepo.findById(id)
				 .orElseThrow(()-> new NotFoundException(String.format("Student %d not found", id)));
	}
    
	@Override
	@Transactional
	public List<Mark> addMark(long id, Mark mark) {
		StudentDoc studentDoc = getStudent(id);
		studentDoc.addMark(mark);
		studentRepo.save(studentDoc);
		log.debug("student {} added mark {}", id, mark);
		return studentDoc.getMarks();
	}

	@Override
	@Transactional
	public Student removeStudent(long id) {
		StudentDoc studentDoc = studentRepo.findStudentNoMarks(id);
		if(studentDoc == null) {
			throw new NotFoundException(String.format("student %d not found", id));
		}
		studentRepo.deleteById(id);
		log.debug("removed student {}, marks {] ", studentDoc.getMarks());
		return studentDoc.build();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Mark> getMarks(long id) {
		StudentDoc studentDoc = studentRepo.findStudentMarks(id);
		if(studentDoc == null) {
			throw new NotFoundException(String.format("student %d not found", id));
		}
		log.debug("id {} name {}, phone {}, marks {}", 
				studentDoc.getId(), studentDoc.getName(),
				studentDoc.getPhone(), studentDoc.getMarks());
		return studentDoc.getMarks();
	}

	@Override
	public Student getStudentByPhone(String phoneNumber) {
		IdName studentDoc = studentRepo.findByPhone(phoneNumber);
		Student res = null;
		if (studentDoc != null) {
			res = new Student(studentDoc.getId(), studentDoc.getName(), phoneNumber);
		}
		return res;
	}

	@Override
	public List<Student> getStudentsByPhonePhonePrefix(String phonePrefix) {
		List<IdNamePhone> students = studentRepo.findByPhoneRegex(phonePrefix + ".+");
		
		log.debug("number of the students having phone prefix {} is {}",
				phonePrefix, students.size());
		return getStudents(students);
	}

	private List<Student> getStudents(List<IdNamePhone> students) {
		return students
				.stream()
				.map(inp -> 
				new Student(inp.getId(), 
						inp.getName(), 
						inp.getPhone()))
				        .toList();
	}

	@Override
	public List<Student> getStudentsAllGoodMarks(int threasholdScore) {
		List<IdNamePhone> students = studentRepo.findByGoodMarks(threasholdScore);
		return getStudents(students);
	}

	@Override
	public List<Student> getStudentFewMarks(int thresholdNMarks) {
		List<IdNamePhone> students = studentRepo.findByFewMarks(thresholdNMarks);
		return getStudents(students);
	}

	@Override
	public List<Student> getStudentsAllGoodMarksSubject(String subject, int thresholdScore) {
	
		//getting students who have at least one score of a given subject and all scores of that subject
				//greater than or equal a given threshold
		List<IdNamePhone> students = studentRepo.findByGoodMarksSubject(subject, thresholdScore);
		
		return getStudents(students);
	}

	@Override
	public List<Student> getStudentsMarksAmountBetween(int min, int max) {
		
		//getting students having number of marks in a closed range of the given values
		//nMarks >= min && nMarks <= max
		List<IdNamePhone> students = studentRepo.findByBetweenMarks(min, max);
		return getStudents(students);
	}

	@Override
	public List<Mark> getStudentSubjectMarks(long id, String subject) {
		if(!studentRepo.existsById(id)) {
			throw new NotFoundException(String.format("student with id %d not found", id));
		}
		MarksOnly marksOnly = studentRepo.findByIdAndMarksSubject(id, subject);
		List<Mark> marks = Collections.emptyList();
		if(marksOnly != null) {
			marks = marksOnly.getMarks();
			log.debug("student %d doesn't have marks of subject {}", id, subject );
		}
		
		log.debug("marks: {}", marks);
		return marks
				.stream().filter(m ->m.subject()
						.equals(subject))
						.toList();
			
	}



}
