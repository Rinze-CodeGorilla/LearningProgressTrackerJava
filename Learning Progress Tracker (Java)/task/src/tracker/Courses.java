package tracker;

public enum Courses {
    Java(600),
    DSA(400),
    Databases(480),
    Spring(550),
    ;

    public final int requirement;

    Courses(int requirement) {

        this.requirement = requirement;
    }

    public int getRequirement() {
        return requirement;
    }
}
