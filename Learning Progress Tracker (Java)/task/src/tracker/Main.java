package tracker;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Learning Progress Tracker");
        var isRunning = true;
        var scanner = new Scanner(System.in);
        while (isRunning) {
            var command = scanner.nextLine();
            switch(command.trim()) {
                case "exit" -> isRunning = false;
                case "" -> System.out.println("No input.");
                default -> System.out.println("Unknown command!");
            }
        }
        System.out.println("Bye!");
    }
}
