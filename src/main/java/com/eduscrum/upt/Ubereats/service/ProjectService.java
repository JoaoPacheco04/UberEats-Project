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

/**
 * Service class for managing projects in the EduScrum platform.
 * Handles project creation, updates, completion, and archival.
 *
 * @version 1.2.0 (2025-12-10)
 */
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CourseRepository courseRepository;
    private final AchievementService achievementService;
    private final TeamService teamService;

    /**
     * Constructs a new ProjectService with required dependencies.
     *
     * @param projectRepository  Repository for project data access
     * @param courseRepository   Repository for course data access
     * @param achievementService Service for achievement operations
     * @param teamService        Service for team operations
     */
    public ProjectService(ProjectRepository projectRepository, CourseRepository courseRepository,
            @Lazy AchievementService achievementService, @Lazy TeamService teamService) {
        this.projectRepository = projectRepository;
        this.courseRepository = courseRepository;
        this.achievementService = achievementService;
        this.teamService = teamService;
    }

    /**
     * Creates a new project associated with a course.
     *
     * @param req The request containing project details
     * @return The created project as a response DTO
     * @throws ResourceNotFoundException if the course is not found
     */
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

    /**
     * Retrieves all non-archived projects.
     *
     * @return List of project response DTOs
     */
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findByStatusNot(ProjectStatus.ARCHIVED).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> getProjectsByCourse(Long courseId) {
        // Return only non-archived projects for the given course
        return projectRepository.findByCourseIdAndStatusNot(courseId, ProjectStatus.ARCHIVED).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id The ID of the project to retrieve
     * @return The project as a response DTO
     * @throws ResourceNotFoundException if the project is not found or is archived
     */
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        if (project.getStatus() == ProjectStatus.ARCHIVED) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }

        return mapToResponse(project);
    }

    /**
     * Updates an existing project with the provided details.
     *
     * @param id  The ID of the project to update
     * @param req The request containing updated project details
     * @return The updated project as a response DTO
     * @throws ResourceNotFoundException if the project is not found
     */
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
     *
     * @param id The ID of the project to archive
     * @throws ResourceNotFoundException if the project is not found
     */
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }

    /**
     * Marks a project as completed, closes team memberships, and triggers badge
     * checks.
     *
     * @param id The ID of the project to complete
     * @return The completed project as a response DTO
     * @throws ResourceNotFoundException if the project is not found
     * @throws BusinessLogicException    if the project is already completed
     */
    public ProjectResponse completeProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        if (project.isCompleted()) {
            throw new BusinessLogicException("Project is already marked as completed.");
        }

        project.setStatus(ProjectStatus.COMPLETED);
        Project updatedProject = projectRepository.save(project);

        // Close team memberships if team is assigned
        if (project.getTeam() != null) {
            teamService.closeTeamMemberships(project.getTeam().getId());
        }

        // Trigger automatic badge checks for project completion
        achievementService.checkAutomaticBadgesOnProjectCompletion(id);

        return mapToResponse(updatedProject);
    }

    /**
     * Maps a Project entity to a ProjectResponse DTO.
     *
     * @param p The project entity to map
     * @return The project response DTO
     */
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
                p.getCourse().getName(),
                p.getTeam() != null ? p.getTeam().getId() : null,
                p.getTeam() != null ? p.getTeam().getName() : null);
    }
}
