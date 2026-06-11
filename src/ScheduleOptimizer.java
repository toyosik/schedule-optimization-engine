import java.util.*;

public class ScheduleOptimizer {

    // Represents a Course (Node/Vertex in the conflict graph)
    public static class Course {
        String id;
        List<String> allowedSlots; // Domain of possible time slots

        public Course(String id, List<String> allowedSlots) {
            this.id = id;
            this.allowedSlots = allowedSlots;
        }
    }

    private final List<Course> courses;
    // Adjacency list representing conflicts (edges). 
    // If course A and B share an instructor or student group, they have an edge.
    private final Map<String, Set<String>> conflictGraph; 

    public ScheduleOptimizer(List<Course> courses) {
        this.courses = courses;
        this.conflictGraph = new HashMap<>();
        for (Course c : courses) {
            conflictGraph.put(c.id, new HashSet<>());
        }
    }

    // Add a conflict edge between two courses (they cannot share the same time slot)
    public void addConflict(String courseId1, String courseId2) {
        if (conflictGraph.containsKey(courseId1) && conflictGraph.containsKey(courseId2)) {
            conflictGraph.get(courseId1).add(courseId2);
            conflictGraph.get(courseId2).add(courseId1);
        }
    }

    // Entry point for the optimization engine
    public Map<String, String> optimize() {
        Map<String, String> assignments = new HashMap<>();
        
        // Sort courses using the MRV (Minimum Remaining Values) heuristic to optimize search path
        List<Course> sortedCourses = new ArrayList<>(courses);
        sortedCourses.sort(Comparator.comparingInt(c -> c.allowedSlots.size()));

        if (backtrack(0, sortedCourses, assignments)) {
            return assignments;
        }
        return null; // Return null if no conflict-free schedule is mathematically possible
    }

    // Recursive CSP backtracking algorithm
    private boolean backtrack(int index, List<Course> sortedCourses, Map<String, String> assignments) {
        // Base Case: All courses have been successfully assigned a conflict-free slot
        if (index == sortedCourses.size()) {
            return true;
        }

        Course currentCourse = sortedCourses.get(index);

        for (String slot : currentCourse.allowedSlots) {
            if (isConsistent(currentCourse.id, slot, assignments)) {
                // Place assignment
                assignments.put(currentCourse.id, slot);

                // Forward Check: Proceed to next course
                if (backtrack(index + 1, sortedCourses, assignments)) {
                    return true;
                }

                // Backtrack: Remove assignment if it led to a dead end down the line
                assignments.remove(currentCourse.id);
            }
        }
        return false;
    }

    // Constraint Validation Check (Graph Node Consistency verification)
    private boolean isConsistent(String courseId, String slot, Map<String, String> assignments) {
        Set<String> conflicts = conflictGraph.get(courseId);
        if (conflicts == null) return true;

        for (String neighborId : conflicts) {
            // If an adjacent node in the graph already has this time slot assigned, it's a conflict
            if (assignments.containsKey(neighborId) && assignments.get(neighborId).equals(slot)) {
                return false; 
            }
        }
        return true;
    }
}
