package project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import project.entity.Project;

public interface ProjectDao extends JpaRepository<Project, Long> {

}
