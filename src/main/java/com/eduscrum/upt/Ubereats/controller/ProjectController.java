package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.CreateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.response.ProjectResponse;
import com.eduscrum.upt.Ubereats.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing projects in the EduScrum platform.
 * Provides endpoints for project CRUD operations and completion.
 *
 * @author UberEats
 * @version 0.5.0
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Constructs a new ProjectController with required dependencies.
     *
     * @param projectService Service for project operations
     */
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Creates a new project.
     *
     * @param req The request containing project details
     * @return ResponseEntity containing the created project
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest req) {
        ProjectResponse createdProject = projectService.createProject(req);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    /**
     * Gets all non-archived projects.
     *
     * @return ResponseEntity containing the list of projects
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ProjectResponse>> getByCourse(@PathVariable Long courseId) {
        List<ProjectResponse> projects = projectService.getProjectsByCourse(courseId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Gets a project by its ID.
     *
     * @param id The ID of the project
     * @return ResponseEntity containing the project
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Updates an existing project.
     *
     * @param id  The ID of the project to update
     * @param req The request containing updated project details
     * @return ResponseEntity containing the updated project
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProjectRequest req) {
        ProjectResponse updatedProject = projectService.updateProject(id, req);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Marks a project as completed.
     *
     * @param id The ID of the project to complete
     * @return ResponseEntity containing the completed project
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ProjectResponse> completeProject(@PathVariable Long id) {
        ProjectResponse projectResponse = projectService.completeProject(id);
        return ResponseEntity.ok(projectResponse);
    }

    /**
     * Archives a project (soft delete).
     *
     * @param id The ID of the project to archive
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
