package tracker;

import java.util.EnumSet;

public final class Student {
    public Student(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int id() {
        return id;
    }

    public EnumSet<Courses> notifications() {
        return notifications;
    }

    int id;
    final String name;
    final String email;
    EnumSet<Courses> notifications = EnumSet.noneOf(Courses.class);
}
