package tracker;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    static final Scanner scanner = new Scanner(System.in);
    static int studentCount = 0;

    public static void main(String[] args) {
        System.out.println("Learning Progress Tracker");
        var isRunning = true;
        while (isRunning) {
            var command = scanner.nextLine();
            switch (command.trim()) {
                case "exit" -> isRunning = false;
                case "back" -> System.out.println("Enter 'exit' to exit the program.");
                case "add students" -> addStudents();
                case "" -> System.out.println("No input.");
                default -> System.out.println("Unknown command!");
            }
        }
        System.out.println("Bye!");
    }

    public static void addStudents() {
        var isAdding = true;
        studentCount = 0;
        System.out.println("Enter student credentials or 'back' to return:");
        while (isAdding) {
            var command = scanner.nextLine().trim();
            switch (command) {
                case "back" -> isAdding = false;
                default -> {
                    System.out.println(addStudent(command));
                }
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
        studentCount++;
        return "The student has been added.";
    }

    static boolean validateEmail(String email) {
        return email.matches("^[A-Za-z.0-9-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9]+$");

    }

    static boolean validateName(String name) {
        if (name.matches(".*['-]['-].*")) return false;
        if (name.matches(".*['-]")) return false;
        if (name.matches("['-].*")) return false;
        return name.matches("[A-Za-z][A-Za-z'-]*[A-Za-z]");
    }
}
