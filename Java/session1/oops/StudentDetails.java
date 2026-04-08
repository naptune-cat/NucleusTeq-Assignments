package session1.oops;

//Parent class
class Student {
    // the vaiables inside a class are called data members
    String name;
    int rollNumber;
    double marks;

    // Constructor for our class which willinitialize the data members
    Student(String name, int rollNumber, double marks) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.marks = marks;
    }

    //functions inside a class are called member functions
    // Method to display student details
    void displayStudentDetails() {
        System.out.println("Name: " + name);
        System.out.println("Roll Number: " + rollNumber);
        System.out.println("Marks: " + marks);
    }
}


// Child class inheriting Student
//extends keyword is used for inheritance in java
class GraduateStudent extends Student {
    String specialization;
    String projectTitle;

    // Constructor
    GraduateStudent(String name, int rollNumber, double marks, String specialization, String projectTitle) {
        // we are calling parent constructor using super keyword
        //super keyword is used to reference the immediate parent of the class it is used in
        // this will fill the name rollNumber and marks in our parent class
        super(name, rollNumber, marks);
        this.specialization = specialization;
        this.projectTitle = projectTitle;
    }

    // Method to display graduate student details
    void displayGraduateDetails() {
        //calling the method of parent class
        displayStudentDetails();
        System.out.println("Specialization: " + specialization);
        System.out.println("Project Title: " + projectTitle);
    }
}
public class StudentDetails {
    public static void main(String[] args) {
        //making object of base/child class
        GraduateStudent gradStudent = new GraduateStudent(
            "Mark John",
            101,
            89.5,
            "Computer Science",
            "AI Based Chatbot"
        );

        gradStudent.displayGraduateDetails();
    }
}
