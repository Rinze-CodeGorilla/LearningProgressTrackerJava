package tracker;

import java.util.*;
import java.util.stream.Collector;

import static java.util.stream.Collectors.*;

public class ActivitiesTracker {
    public record Activity(Courses course, Student student, int score) {}
    public record StudentScore(Student student, int score) {}
    public record Result(String mostStudents, String leastStudents, String mostActive, String leastActive, String easiestCourse, String hardestCourse) {}
    ArrayList<Activity> activities = new ArrayList<>();
    public void addActivity(Courses course, Student student, int score) {
        if (score > 0) activities.add(new Activity(course, student, score));
    }

    public Map<Courses, Integer> getStudent(Student student) {
        return activities.stream().filter(a -> a.student.equals(student)).collect(groupingBy(Activity::course, summingInt(Activity::score)));
    }

    public List<StudentScore> getCourse(Courses course) {
        Comparator<StudentScore> comp = (a, b) -> {
            if (a.equals(b)) return 0;
            if (a.score == b.score) return Integer.compare(a.student.id(), b.student.id());
            return Integer.compare(b.score, a.score);
        };
        return activities.stream().filter(a -> a.course.equals(course)).collect(groupingBy(Activity::student, summingInt(Activity::score))).entrySet().stream().map(e -> new StudentScore(e.getKey(), e.getValue())).sorted(comp).toList();
    }

    public Result getStats() {
        record Accumulating(Set<Student> students, IntSummaryStatistics activities) {}
        record Accumulated(int students, int activities, double average) {}
        Collector<Activity, EnumMap<Courses, Accumulating>, EnumMap<Courses, Accumulated>> cijferaar = Collector.of(
                () -> new EnumMap<>(Courses.class),
                (acc, item) -> {
                    Accumulating a = acc.getOrDefault(item.course(), new Accumulating(new HashSet<>(), new IntSummaryStatistics()));
                    a.students.add(item.student());
                    a.activities.accept(item.score());
                    acc.putIfAbsent(item.course(), a);
                },
                (left, right) -> {
                    right.forEach((k, v) -> left.merge(k, v, (v1, v2) -> {
                        v1.activities.combine(v2.activities);
                        v1.students.addAll(v2.students);
                        return v1;
                    }));
                    return left;
                },
                acc -> new EnumMap<Courses, Accumulated>(EnumSet.allOf(Courses.class).stream().collect(toMap(c -> c, c -> {
                    if (acc.containsKey(c)) {
                        var a = acc.get(c);
                        return new Accumulated(a.students.size(), (int) a.activities.getCount(), a.activities.getAverage());
                    }
                    return new Accumulated(0, 0, Double.NaN);
                })))
        );
        var stats = activities.stream().collect(cijferaar);
        var studentStats = stats.values().stream().collect(summarizingInt(Accumulated::students));
        var activityStats = stats.values().stream().collect(summarizingInt(Accumulated::activities));
        var difficultyStats = stats.values().stream().filter(a -> !Double.isNaN(a.average)).collect(summarizingDouble(Accumulated::average));
        if (studentStats.getSum() == 0) return new Result("n/a", "n/a", "n/a", "n/a", "n/a", "n/a");
        var mostStudentsCourses = stats.entrySet().stream().filter(e -> e.getValue().students == studentStats.getMax()).map(e -> e.getKey().name()).collect(joining(", ")).transform(s -> s.isEmpty() ? "n/a" : s);
        var leastStudentsCourses = stats.entrySet().stream().filter(e -> e.getValue().students == studentStats.getMin()).filter(e -> e.getValue().students != studentStats.getMax()).map(e -> e.getKey().name()).collect(joining(", ")).transform(s -> s.isEmpty() ? "n/a" : s);
        var mostActivitiesCourses = stats.entrySet().stream().filter(e -> e.getValue().activities == activityStats.getMax()).map(e -> e.getKey().name()).collect(joining(", ")).transform(s -> s.isEmpty() ? "n/a" : s);
        var leastActivitiesCourses = stats.entrySet().stream().filter(e -> e.getValue().activities == activityStats.getMin()).filter(e -> e.getValue().activities != activityStats.getMax()).map(e -> e.getKey().name()).collect(joining(", ")).transform(s -> s.isEmpty() ? "n/a" : s);
        var leastDifficultCourses = stats.entrySet().stream().filter(e -> e.getValue().average == difficultyStats.getMax()).map(e -> e.getKey().name()).collect(joining(", ")).transform(s -> s.isEmpty() ? "n/a" : s);
        var mostDifficultCourses = stats.entrySet().stream().filter(e -> e.getValue().average == difficultyStats.getMin()).filter(e -> e.getValue().average != difficultyStats.getMax()).map(e -> e.getKey().name()).collect(joining(", ")).transform(s -> s.isEmpty() ? "n/a" : s);
        return new Result(mostStudentsCourses, leastStudentsCourses, mostActivitiesCourses, leastActivitiesCourses, leastDifficultCourses, mostDifficultCourses);
    }
}
