package tracker;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static final Scanner scanner = new Scanner(System.in);
    static int studentCount = 0;
    static ArrayList<Student> students = new ArrayList<>();
    static ActivitiesTracker activities = new ActivitiesTracker();
    static Set<String> emails = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Learning Progress Tracker");
        var isRunning = true;
        while (isRunning) {
            var command = scanner.nextLine();
            switch (command.trim()) {
                case "exit" -> isRunning = false;
                case "back" -> System.out.println("Enter 'exit' to exit the program.");
                case "add students" -> addStudents();
                case "list" -> listStudents();
                case "add points" -> addPoints();
                case "find" -> find();
                case "statistics" -> statistics();
                case "" -> System.out.println("No input.");
                default -> System.out.println("Unknown command!");
            }
        }
        System.out.println("Bye!");
    }

    static void statistics() {
        var stats = activities.getStats();
        System.out.printf("""
                        Type the name of a course to see details or 'back' to quit:
                        Most popular: %s
                        Least popular: %s
                        Highest activity: %s
                        Lowest activity: %s
                        Easiest course: %s
                        Hardest course: %s
                        """,
                stats.mostStudents(), stats.leastStudents(),
                stats.mostActive(), stats.leastActive(),
                stats.easiestCourse(), stats.hardestCourse()
        );
        var acting = true;
        while (acting) {
            var command = scanner.nextLine();
            try {
                switch (command) {
                    case "back" -> acting = false;
                    default -> findCourse(Courses.valueOf(command));
                }
            } catch (Exception e) {
                System.out.println("Unknown course.");
            }
        }
    }

    static void findCourse(Courses course) {
        System.out.println(course.name());
        var format = "%-6s%-8s%s%n";
        System.out.format(format, "id", "points", "completed");
        activities.getCourse(course).forEach(ss -> {
            System.out.printf(format, ss.student().id(), ss.score(), "%1.1f%%".formatted( 100f * ss.score() / course.requirement));
        });
    }

    static void find() {
        System.out.println("Enter an id or 'back' to return:");
        var isFinding = true;
        while (isFinding) {
            var command = scanner.nextLine();
            switch (command) {
                case "back" -> isFinding = false;
                default -> System.out.println(findStudent(command));
            }
        }
    }

    static String findStudent(String studentId) {
        try {
            var id = Integer.parseUnsignedInt(studentId);
            var student = students.get(id);
            var data = activities.getStudent(student);
            var java = data.getOrDefault(Courses.Java, 0);
            var dsa = data.getOrDefault(Courses.DSA, 0);
            var database = data.getOrDefault(Courses.Databases, 0);
            var spring = data.getOrDefault(Courses.Spring, 0);
            return "%d points: Java=%d; DSA=%d; Databases=%d; Spring=%d".formatted(id, java, dsa, database, spring);
        } catch (Exception e) {
            return "No student is found for id=%s".formatted(studentId);
        }
    }

    static void addPoints() {
        System.out.println("Enter an id and points or 'back' to return:");
        var isAdding = true;
        while (isAdding) {
            var command = scanner.nextLine();
            switch (command) {
                case "back" -> isAdding = false;
                default -> System.out.println(addPoint(command));
            }
        }
    }

    static String addPoint(String pointsString) {
        var parts = pointsString.split(" ");
        Student student;
        try {
            if (parts.length != 5) return "Incorrect points format.";
            student = students.get(Integer.parseInt(parts[0]));
        } catch (Exception e) {
            return "No student found for id=%s".formatted(parts[0]);
        }
        try {
            var parsed = Arrays.stream(parts).mapToInt(Integer::parseUnsignedInt).toArray();
            activities.addActivity(Courses.Java, student, parsed[1]);
            activities.addActivity(Courses.DSA, student, parsed[2]);
            activities.addActivity(Courses.Databases, student, parsed[3]);
            activities.addActivity(Courses.Spring, student, parsed[4]);
            return "Points updated.";
        } catch (Exception e) {
            return "Incorrect points format.";
        }
    }

    static void listStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("Students:");
        for (int i = 0; i < students.size(); i++) {
            System.out.println(i);
        }
    }

    static void addStudents() {
        var isAdding = true;
        studentCount = 0;
        System.out.println("Enter student credentials or 'back' to return:");
        while (isAdding) {
            var command = scanner.nextLine().trim();
            switch (command) {
                case "back" -> isAdding = false;
                default -> System.out.println(addStudent(command));
            }
        }
        System.out.println("Total %d students have been added".formatted(studentCount));
    }

    static String addStudent(String credentials) {
        var parts = credentials.split(" ");
        if (parts.length < 3)
            return "Incorrect credentials.";
        var firstName = parts[0];
        var lastNameParts = Arrays.stream(parts).skip(1).limit(parts.length - 2).toList();
        var lastName = Arrays.stream(parts).skip(1).limit(parts.length - 2).collect(Collectors.joining(" "));
        var email = parts[parts.length - 1];
        if (!validateName(firstName))
            return "Incorrect first name.";
        if (!lastNameParts.stream().allMatch(Main::validateName))
            return "Incorrect last name.";
        if (!validateEmail(email))
            return "Incorrect email.";
        if (!emails.add(email))
            return "This email is already taken.";
        studentCount++;
        students.add(new Student(students.size()));
        return "The student has been added.";
    }

    static boolean validateEmail(String email) {
        return email.matches("^[A-Za-z.0-9-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9]+$");

    }

    static boolean validateName(String name) {
        if (name.matches(".*['-]['-].*")) return false;
        if (name.matches(".*['-]")) return false;
        if (name.matches("['-].*")) return false;
        return name.matches("[\\w][A-Za-z'-]*[A-Za-z]");
    }
}
