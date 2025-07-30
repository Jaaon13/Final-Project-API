package project.controller.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import project.entity.Project;
import project.entity.Step;
import project.entity.User;

@Data
@NoArgsConstructor
public class ProjectData {
	
	private Long projectId;
	
	private String name;
	private String description;
	private LocalTime estimatedTime;
	private LocalTime actualTime;
	private LocalDateTime createdAt;
	private Set<UserData> users = new HashSet<>();
	private Set<StepData> steps = new HashSet<>();

	public ProjectData(Project p) {
		
		this.projectId = p.getProjectId();
		this.name = p.getName();
		this.description = p.getDescription();
		this.estimatedTime = p.getEstimatedTime();
		this.actualTime = p.getActualTime();
		this.createdAt = p.getCreatedAt();
		
		for(Step s : p.getSteps()) { this.steps.add(new StepData(s)); } // Does Not Contain A Project Object No Need For The Boolean
		
		for(User u : p.getUsers()) { this.users.add(new UserData(u, false)); } // Prevents Recursion
		
		
	}

	public Project toProject() { // Returns The Entity
		Project p = new Project();
		
		p.setProjectId(projectId);
		p.setName(name);
		p.setDescription(description);
		p.setEstimatedTime(estimatedTime);
		p.setActualTime(actualTime);
		p.setCreatedAt(createdAt);
		
		for(StepData s : steps) {
			
			p.getSteps().add(s.toStep());
			
		}
		
		for(UserData u : users) {
			
			p.getUsers().add(u.toUser());
			
		}
		
		return p;
	}
	
}
