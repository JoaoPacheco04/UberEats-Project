package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}