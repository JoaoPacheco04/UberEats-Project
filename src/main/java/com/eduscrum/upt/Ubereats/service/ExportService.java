package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
public class ExportService {

    private final UserRepository userRepository;
    private final UserService userService;

    public ExportService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Generate CSV for a course
     * Header: "Name, Student Number, Global Score, Badges Count"
     */
    public byte[] generateCourseCsv(Long courseId) {
        List<User> students = userRepository.findStudentsByCourseId(courseId);

        StringBuilder csvBuilder = new StringBuilder();
        // Add Header
        csvBuilder.append("Name,Student Number,Global Score,Badges Count\n");

        for (User student : students) {
            String name = escapeCsv(student.getFullName());
            String studentNumber = escapeCsv(student.getStudentNumber() != null ? student.getStudentNumber() : "");
            Integer globalScore = userService.calculateGlobalScore(student.getId());
            // Using getEarnedBadges().size() for unique badges count
            int badgesCount = student.getEarnedBadges().size();

            csvBuilder.append(name).append(",")
                    .append(studentNumber).append(",")
                    .append(globalScore).append(",")
                    .append(badgesCount).append("\n");
        }

        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
