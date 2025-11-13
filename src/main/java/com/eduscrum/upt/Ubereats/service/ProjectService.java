package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.CreateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.response.ProjectResponse;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CourseRepository courseRepository;

    public ProjectService(ProjectRepository projectRepository, CourseRepository courseRepository) {
        this.projectRepository = projectRepository;
        this.courseRepository = courseRepository;
    }

    public ProjectResponse createProject(CreateProjectRequest req) {

        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + req.getCourseId()));

        Project project = new Project(
                req.getName(),
                req.getDescription(),
                req.getStartDate(),
                req.getEndDate(),
                course
        );

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public ProjectResponse getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    public ProjectResponse updateProject(Long id, UpdateProjectRequest req) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        if (req.getName() != null) project.setName(req.getName());
        if (req.getDescription() != null) project.setDescription(req.getDescription());
        if (req.getStartDate() != null) project.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) project.setEndDate(req.getEndDate());

        return mapToResponse(projectRepository.save(project));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private ProjectResponse mapToResponse(Project p) {
        return new ProjectResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStartDate(),
                p.getEndDate(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getCourse().getId(),
                p.getCourse().getName()
        );
    }



}