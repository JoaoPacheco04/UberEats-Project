package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.entity.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Project entity.
 * Provides CRUD operations and custom queries.
 *
 * @version 1.2.0 (2025-12-10)
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatusNot(ProjectStatus status);
}
