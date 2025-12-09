package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.CreateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.response.ProjectResponse;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.entity.enums.ProjectStatus;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import com.eduscrum.upt.Ubereats.exception.ResourceNotFoundException;
import com.eduscrum.upt.Ubereats.exception.BusinessLogicException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CourseRepository courseRepository;
    private final AchievementService achievementService;
    private final TeamService teamService;

    public ProjectService(ProjectRepository projectRepository, CourseRepository courseRepository,
            @Lazy AchievementService achievementService, @Lazy TeamService teamService) {
        this.projectRepository = projectRepository;
        this.courseRepository = courseRepository;
        this.achievementService = achievementService;
        this.teamService = teamService;
    }

    public ProjectResponse createProject(CreateProjectRequest req) {

        Course course = courseRepository.findById(req.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + req.getCourseId()));

        Project project = new Project(
                req.getName(),
                req.getDescription(),
                req.getStartDate(),
                req.getEndDate(),
                course);

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    public List<ProjectResponse> getAllProjects() {
        // Return only non-archived projects
        return projectRepository.findByStatusNot(ProjectStatus.ARCHIVED).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        // Do not return archived projects through this standard getter
        if (project.getStatus() == ProjectStatus.ARCHIVED) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }

        return mapToResponse(project);
    }

    public ProjectResponse updateProject(Long id, UpdateProjectRequest req) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        if (req.getName() != null)
            project.setName(req.getName());
        if (req.getDescription() != null)
            project.setDescription(req.getDescription());
        if (req.getStartDate() != null)
            project.setStartDate(req.getStartDate());
        if (req.getEndDate() != null)
            project.setEndDate(req.getEndDate());

        return mapToResponse(projectRepository.save(project));
    }

    /**
     * Archives a project instead of deleting it permanently.
     */
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }

    /**
     * Marks the project status as COMPLETED.
     */
    public ProjectResponse completeProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        if (project.isCompleted()) {
            throw new BusinessLogicException("Project is already marked as completed.");
        }

        project.setStatus(ProjectStatus.COMPLETED);
        Project updatedProject = projectRepository.save(project);

        // Close all team memberships
        project.getTeams().forEach(team -> teamService.closeTeamMemberships(team.getId()));

        // Trigger automatic badge checks for project completion
        achievementService.checkAutomaticBadgesOnProjectCompletion(id);

        return mapToResponse(updatedProject);
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
                p.getCourse().getName());
    }
}