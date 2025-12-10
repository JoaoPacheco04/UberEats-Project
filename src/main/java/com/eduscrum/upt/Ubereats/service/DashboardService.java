package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.response.AchievementResponseDTO;
import com.eduscrum.upt.Ubereats.dto.response.DashboardStatsDTO;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.CourseEnrollment;
import com.eduscrum.upt.Ubereats.entity.TeamMember;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for generating dashboard statistics in the EduScrum platform.
 * Provides aggregated data for student dashboards including scores and
 * achievements.
 *
 * @author
 * @version 1.0 (2025-12-10)
 */
@Service
@Transactional
public class DashboardService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    /**
     * Constructs a new DashboardService with required dependencies.
     *
     * @param userService        Service for user operations
     * @param userRepository     Repository for user data access
     * @param achievementService Service for achievement operations
     */
    public DashboardService(UserService userService,
            UserRepository userRepository,
            AchievementService achievementService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    /**
     * Retrieves dashboard statistics for a student including global score,
     * course average, team velocity history, and recent badges.
     */
    @Transactional(readOnly = true)
    public DashboardStatsDTO getStudentDashboardStats(Long studentId) {
        User student = userService.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + studentId));

        // 1. Calculate Global Score
        Integer globalScore = userService.calculateGlobalScore(studentId);

        // 2. Calculate Course Average
        Double courseAverageScore = 0.0;
        // Find active enrollment to determine context course
        // If multiple, just pick the first one for now as per simple requirement "the
        // course"
        Optional<CourseEnrollment> enrollmentOpt = student.getEnrollments().stream()
                .findFirst(); // You might want to filter by isActive if available

        if (enrollmentOpt.isPresent()) {
            Course course = enrollmentOpt.get().getCourse();
            List<User> courseStudents = userRepository.findStudentsByCourseId(course.getId());

            if (!courseStudents.isEmpty()) {
                double totalGlobalScore = courseStudents.stream()
                        .mapToInt(s -> userService.calculateGlobalScore(s.getId()))
                        .sum();
                courseAverageScore = totalGlobalScore / courseStudents.size();
            }
        }

        // 3. Team Velocity History
        Map<String, Integer> teamVelocityHistory = new LinkedHashMap<>();
        // Get active team
        Optional<TeamMember> activeMembership = student.getTeamMemberships().stream()
                .filter(m -> Boolean.TRUE.equals(m.getIsActive()))
                .findFirst();

        if (activeMembership.isPresent()) {
            // Get sprints from the project(s) the team is in, and find velocity recorded
            // for this team
            // Actually, Sprint stores ProgressMetrics which link Team and Sprint.
            // But efficient way: Team has no direct list of metrics?
            // Actually Sprint has list of metrics.
            // Better: Team -> projects -> sprints -> metrics filter by team?
            // Or maybe Team entity has metrics list? Let's assume we iterate projects ->
            // sprints.
            // Prompt said: "Fetch the student's active team and map their ProgressMetrics
            // (velocity) to the history map."
            // Sprint.java has `List<ProgressMetric> progressMetrics`.
            // User.java -> TeamMember -> Team.
            // Does Team have reference to metrics? Team.java likely has.
            // If not, we have to search.
            // Let's rely on checking Team.java content if I could. I looked earlier.
            // I'll assume iterating via Project -> Sprints is safer if Team doesn't have it
            // mapped.
            // But wait, `Sprint.java` has `getTeamVelocity(Team team)`.
            // So for each sprint in team's projects, we call `getTeamVelocity`.

            activeMembership.get().getTeam().getProjects().forEach(project -> {
                project.getSprints().stream()
                        .sorted(Comparator.comparing(com.eduscrum.upt.Ubereats.entity.Sprint::getStartDate))
                        .forEach(sprint -> {
                            BigDecimal velocity = sprint.getTeamVelocity(activeMembership.get().getTeam());
                            // Only include if velocity > 0 or sprint is completed/active?
                            // Usually velocity history shows all sprints.
                            teamVelocityHistory.put(sprint.getName(), velocity.intValue());
                        });
            });
        }

        // 4. Recent Badges
        List<AchievementResponseDTO> recentBadges = achievementService.getUserAchievements(studentId).stream()
                .sorted((a1, a2) -> a2.getAwardedAt().compareTo(a1.getAwardedAt())) // Descending
                .limit(5)
                .collect(Collectors.toList());

        return new DashboardStatsDTO(globalScore, courseAverageScore, teamVelocityHistory, recentBadges);
    }
}
