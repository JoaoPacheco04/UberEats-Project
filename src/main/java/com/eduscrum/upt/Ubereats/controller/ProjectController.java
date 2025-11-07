package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.CreateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.response.ProjectResponse;
import com.eduscrum.upt.Ubereats.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;
    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest req) {
        return ResponseEntity.ok(projectService.createProject(req));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable Long id, @RequestBody UpdateProjectRequest req) {
        return ResponseEntity.ok(projectService.updateProject(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}