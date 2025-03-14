package com.advpro.profiling.tutorial.service;

import com.advpro.profiling.tutorial.model.Student;
import com.advpro.profiling.tutorial.model.StudentCourse;
import com.advpro.profiling.tutorial.repository.StudentCourseRepository;
import com.advpro.profiling.tutorial.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author muhammad.khadafi
 */
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    public List<StudentCourse> getAllStudentsWithCourses() {
        List<Student> students = studentRepository.findAll();
        List<StudentCourse> studentCourses = new ArrayList<>();
        
        // Fetch all student courses in one database call
        List<StudentCourse> allStudentCourses = studentCourseRepository.findAll();
        
        // Create a map to group courses by student ID for efficient lookup
        Map<Long, List<StudentCourse>> coursesByStudentId = new HashMap<>();
        for (StudentCourse sc : allStudentCourses) {
            Long studentId = sc.getStudent().getId();
            if (!coursesByStudentId.containsKey(studentId)) {
                coursesByStudentId.put(studentId, new ArrayList<>());
            }
            coursesByStudentId.get(studentId).add(sc);
        }
        
        // Process each student with their courses from the map
        for (Student student : students) {
            List<StudentCourse> studentCoursesByStudent = coursesByStudentId.getOrDefault(student.getId(), new ArrayList<>());
            for (StudentCourse studentCourseByStudent : studentCoursesByStudent) {
                StudentCourse studentCourse = new StudentCourse();
                studentCourse.setStudent(student);
                studentCourse.setCourse(studentCourseByStudent.getCourse());
                studentCourses.add(studentCourse);
            }
        }
        return studentCourses;
    }

    public Optional<Student> findStudentWithHighestGpa() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .max((s1, s2) -> Double.compare(s1.getGpa(), s2.getGpa()));
    }

    public String joinStudentNames() {
        List<Student> students = studentRepository.findAll();
        String result = "";
        for (Student student : students) {
            result += student.getName() + ", ";
        }
        return result.substring(0, result.length() - 2);
    }
}

