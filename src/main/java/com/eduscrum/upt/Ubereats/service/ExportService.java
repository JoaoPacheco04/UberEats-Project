package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service class for exporting data from the EduScrum platform.
 * Handles generation of CSV files for courses and student data.
 *
 * @author Ana
 * @author Francisco
 * @version 1.1.0 (2025-12-08)
 */
@Service
@Transactional
public class ExportService {

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Constructs a new ExportService with required dependencies.
     *
     * @param userRepository Repository for user data access
     * @param userService    Service for user operations
     */
    public ExportService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Generates a CSV file containing student data for a specific course.
     * Includes student name, student number, global score, and badge count.
     *
     * @param courseId The ID of the course to export data from
     * @return CSV file content as byte array
     */
    public byte[] generateCourseCsv(Long courseId) {
        List<User> students = userRepository.findStudentsByCourseId(courseId);

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Name,Student Number,Global Score,Badges Count\n");

        for (User student : students) {
            String name = escapeCsv(student.getFullName());
            String studentNumber = escapeCsv(student.getStudentNumber() != null ? student.getStudentNumber() : "");
            Integer globalScore = userService.calculateGlobalScore(student.getId());
            int badgesCount = student.getEarnedBadges().size();

            csvBuilder.append(name).append(",")
                    .append(studentNumber).append(",")
                    .append(globalScore).append(",")
                    .append(badgesCount).append("\n");
        }

        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Escapes special characters in CSV values to ensure proper formatting.
     *
     * @param value The value to escape
     * @return The escaped value safe for CSV format
     */
    private String escapeCsv(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
