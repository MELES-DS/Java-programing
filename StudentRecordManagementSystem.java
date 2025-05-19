import java.io.*;
import java.util.*;

class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private int rollNumber;
    private String id;
    private String name;
    private String sex;
    private double grade;
    private String subject;

    public Student(int rollNumber, String id, String name, String sex, double grade, String subject) {
        this.rollNumber = rollNumber;
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.grade = grade;
        this.subject = subject;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Grade: " + grade + ", Subject: " + subject;
    }
}

public class StudentRecordManagementSystem {
    private static final String FILE_PATH = "students.dat";
    private static List<Student> students = Collections.synchronizedList(new ArrayList<>());
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            loadStudents();
            while (true) {
                displayMenu();
                int choice = getValidChoice();
                switch (choice) {
                    case 1:
                        addStudent();
                        break;
                    case 2:
                        deleteStudent();
                        break;
                    case 3:
                        updateStudent();
                        break;
                    case 4:
                        displayStudents();
                        break;
                    case 5:
                        saveStudents();
                        System.out.println("Exiting program...");
                        scanner.close();
                        return;
                }
                saveStudents();
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void loadStudents() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            students = (List<Student>) ois.readObject();
        } catch (FileNotFoundException e) {
            students = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private static void saveStudents() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Student Record Management System ===");
        System.out.println("1. Add Student");
        System.out.println("2. Delete Student");
        System.out.println("3. Update Student");
        System.out.println("4. Display All Students");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }

    private static int getValidChoice() {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= 5) {
                    return choice;
                }
                System.out.print("Invalid choice. Please enter a number between 1 and 5: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static void addStudent() {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();
        if (findStudent(id) != null) {
            System.out.println("Student with ID " + id + " already exists!");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Student Sex (M/F): ");
        String sex = scanner.nextLine().toUpperCase();
        while (!sex.equals("M") && !sex.equals("F")) {
            System.out.print("Invalid input. Please enter M for Male or F for Female: ");
            sex = scanner.nextLine().toUpperCase();
        }

        double grade;
        while (true) {
            try {
                System.out.print("Enter Grade (0-100): ");
                grade = Double.parseDouble(scanner.nextLine());
                if (grade >= 0 && grade <= 100) {
                    break;
                }
                System.out.println("Grade must be between 0 and 100!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid grade format!");
            }
        }
        System.out.print("Enter Subject: ");
        String subject = scanner.nextLine();

        // Generate roll number based on current size of list
        synchronized (students) {
            int rollNumber = students.size() + 1;
            students.add(new Student(rollNumber, id, name, sex, grade, subject));
        }
        System.out.println("Student added successfully!");
    }

    private static void deleteStudent() {
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine();
        Student student = findStudent(id);
        if (student == null) {
            System.out.println("Student with ID " + id + " not found!");
            return;
        }
        students.remove(student);
        System.out.println("Student deleted successfully!");
    }

    private static void updateStudent() {
        System.out.print("Enter Student ID to update: ");
        String id = scanner.nextLine();
        Student student = findStudent(id);
        if (student == null) {
            System.out.println("Student with ID " + id + " not found!");
            return;
        }

        System.out.print("Enter new Name (leave blank to keep '" + student.getName() + "'): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            student.setName(name);
        }

        System.out.print("Enter new Sex (leave blank to keep '" + student.getSex() + "'): ");
        String sex = scanner.nextLine();
        if (!sex.isEmpty()) {
            sex = sex.toUpperCase();
            while (!sex.equals("M") && !sex.equals("F")) {
                System.out.print("Invalid input. Please enter M for Male or F for Female: ");
                sex = scanner.nextLine().toUpperCase();
            }
            student.setSex(sex);
        }

        System.out.print("Enter new Grade (enter -1 to keep " + student.getGrade() + "): ");
        while (true) {
            try {
                double grade = Double.parseDouble(scanner.nextLine());
                if (grade == -1)
                    break;
                if (grade >= 0 && grade <= 100) {
                    student.setGrade(grade);
                    break;
                }
                System.out.println("Grade must be between 0 and 100!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid grade format!");
            }
        }

        System.out.print("Enter new Subject (leave blank to keep '" + student.getSubject() + "'): ");
        String subject = scanner.nextLine();
        if (!subject.isEmpty()) {
            student.setSubject(subject);
        }

        System.out.println("Student updated successfully!");
    }

    private static void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("No students recorded!");
            return;
        }
        
        System.out.println("\n=== All Students ===\n");
        System.out.println("Roll\tID\t\tName\t\tSex\tGrade\tSubject");
        System.out.println("=========================================================");
        
        synchronized (students) {
            int rowNumber = 1;
            for (Student student : students) {
                System.out.printf("%d\t%s\t%s\t%s\t%.2f\t%s\n",
                        rowNumber++,
                        student.getId(),
                        student.getName(),
                        student.getSex(),
                        student.getGrade(),
                        student.getSubject());
            }
        }
    }

    private static Student findStudent(String id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }
}
