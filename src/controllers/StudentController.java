package controllers;

import FileManager.FileManager;
import model.*;

import java.util.ArrayList;

public class StudentController {

    private FileManager fm;
    private ArrayList<Course> courseList;
    private ArrayList<Student> studentList;

    public StudentController(){
        fm = new FileManager();
    }

    public String registerForCourse(String username, String courseCode, int indexNumber){
        courseList = fm.read_course();
        studentList = fm.read_student();

        Student stud = getStudentByUsername(username);
        Course c = getCourseByCode(courseCode);

        // Check if student already registered for this course
        for (Index i: stud.getRegistered())
            if (i.getCourse().getCourse_code().equals(courseCode))
                return "You have already registered for this course!";

        // Check if student already waitlisted for this course
        for (Index i: stud.getWaitlisted())
            if (i.getCourse().getCourse_code().equals(courseCode))
                return "You are currently in the waitlist for this course!";

        //TODO Check for clash before registering/waitlisting
        for (Index i: c.getIndexes()){
            // find the index
            if (i.getIndex_number() == indexNumber) {
                // have vacancy, register
                if (i.getMax_capacity() - i.getEnrolledStudents().size() > 0) {
                    i.getEnrolledStudents().add(stud);
                    stud.getRegistered().add(i);
                    fm.write_course(courseList);
                    fm.write_student(studentList);
                    return "Registration successful!";
                // no vacancy, waitlist
                }else{
                    i.getWaitlistedStudents().add(stud);
                    stud.getWaitlisted().add(i);
                    fm.write_course(courseList);
                    fm.write_student(studentList);
                    return "The selected index is currently full! You have been added to the waitlist.";
                }
            }
        }
        return "The index number you entered was not found in the course.";
    }

    public String dropRegisteredCourse(String username, String courseCode, int indexNumber){
        courseList = fm.read_course();
        studentList = fm.read_student();

        Student stud = getStudentByUsername(username);
        Course c = getCourseByCode(courseCode);

        // remove index from student
        boolean registered = false;
        for (Index i: stud.getRegistered()){
            if (i.getIndex_number() == indexNumber && i.getCourse().getCourse_code().equals(courseCode)){
                registered = true;
                stud.getRegistered().remove(i);
                break;
            }
        }

        // if index was not found in student, return error
        if (!registered)
            return "You are not registered for that index!";

        // remove student from the index
        for (Index i: c.getIndexes()){
            if (i.getIndex_number()==indexNumber){
                for (Student x: i.getEnrolledStudents())
                    if (x.getUsername().equals(username)) {
                        i.getEnrolledStudents().remove(x);
                        System.out.println("Removed");
                        System.out.println("No. of enrolled sutdnets: "+ i.getEnrolledStudents().size());
                        break;
                    }
            }
        }

        fm.write_student(studentList);
        fm.write_course(courseList);

        return "You have successfully dropped the course!";
    }

    private Student getStudentByUsername(String username){
        for (Student stud: studentList)
            if (stud.getUsername().equals(username))
                return stud;
        return null;
    }

    private Course getCourseByCode(String courseCode){
        for(Course course: courseList)
        {
            if(course.getCourse_code().equals(courseCode))
                return course;
        }
        return null;
    }

}
