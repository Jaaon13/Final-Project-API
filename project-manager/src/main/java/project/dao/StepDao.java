package project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import project.entity.Step;

public interface StepDao extends JpaRepository<Step, Long> {

}
