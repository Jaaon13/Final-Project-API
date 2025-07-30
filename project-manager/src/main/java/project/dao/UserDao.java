package project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import project.entity.User;

public interface UserDao extends JpaRepository<User, Long> {

}
