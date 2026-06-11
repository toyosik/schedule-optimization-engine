import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing Schedule Optimization Engine simulation...");

        // Define available time slots (Domains)
        List<String> timeSlots = Arrays.asList("Slot_A", "Slot_B", "Slot_C", "Slot_D", "Slot_E");

        // Scale factor: 2,000 courses with 5 slot options each = 10,000 combinations
        int totalCourses = 2000; 
        List<ScheduleOptimizer.Course> courses = new ArrayList<>();

        for (int i = 0; i < totalCourses; i++) {
            courses.add(new ScheduleOptimizer.Course("CS_" + i, timeSlots));
        }

        ScheduleOptimizer optimizer = new ScheduleOptimizer(courses);

        // Inject realistic random scheduling constraints/conflicts into the graph
        Random rand = new Random(42); // Seeded for consistency
        for (int i = 0; i < totalCourses; i++) {
            // Give each course 3 random conflicting neighbors to build a complex graph matrix
            for (int j = 0; j < 3; j++) {
                int conflictTarget = rand.nextInt(totalCourses);
                if (conflictTarget != i) {
                    optimizer.addConflict("CS_" + i, "CS_" + conflictTarget);
                }
            }
        }

        System.out.println("Running graph constraint-solving calculations...");
        
        // Performance Profiling Execution
        long startTime = System.nanoTime();
        Map<String, String> solution = optimizer.optimize();
        long endTime = System.nanoTime();

        double executionTimeMs = (endTime - startTime) / 1_000_000.0;

        if (solution != null) {
            System.out.println("\n=== OPTIMIZATION SUCCESSFUL ===");
            System.out.printf("Total Combinations Tracked: %,d%n", totalCourses * timeSlots.size());
            System.out.printf("Execution Engine Time: %.2f ms%n", executionTimeMs);
            System.out.println("Sample Assignments (First 5 courses):");
            for (int i = 0; i < 5; i++) {
                String courseId = "CS_" + i;
                System.out.println("  " + courseId + " assigned to -> " + solution.get(courseId));
            }
        } else {
            System.out.println("Optimization failed: Mathematical dead-end. No conflict-free matrix exists.");
        }
    }
}
