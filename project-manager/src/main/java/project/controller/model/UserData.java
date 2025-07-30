package project.controller.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import project.entity.Project;
import project.entity.User;

@Data
@NoArgsConstructor
public class UserData {
	
	private Long userId;
	private String name;
	private String password;
	private Set<ProjectData> projects = new HashSet<>();
	
	public UserData(User u, boolean needProject) {
		
		this.userId = u.getUserId();
		this.name = u.getName();
		this.password = u.getPassword();
		
		if(needProject) { // Prevents An Infinite Recursion
			for(Project p : u.getProjects()) { this.projects.add(new ProjectData(p)); }
		}
		
	}
	
	public User toUser() { // Returns The Entity
		User u = new User();
		
		u.setUserId(userId);
		u.setName(name);
		u.setPassword(password);
		
		for(ProjectData p : projects) {
			
			u.getProjects().add(p.toProject());
			
		}
		
		return u;
	}
	
}
