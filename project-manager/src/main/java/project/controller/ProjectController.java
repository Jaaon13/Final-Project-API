package project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import project.controller.model.ProjectData;
import project.controller.model.StepData;
import project.controller.model.UserData;
import project.service.ProjectService;

@RestController
@RequestMapping("/user_project")
@Slf4j
public class ProjectController {
	
	@Autowired
	private ProjectService pS;
	
	// Creates A User
	@PostMapping("/user")
	@ResponseStatus(code = HttpStatus.CREATED)
	public UserData createUser(@RequestBody UserData userData) {
		
		log.info("Creating User: {}", userData);
			
		return pS.createUser(userData);
		
	}
	
	// Gets All Users
	@GetMapping("/user")
	public List<UserData> getUsers() {
		
		log.info("Getting all Users");
		
		return pS.getAllUsers(true);
		
	}
	
	// Gets User Using Name & Password
	@GetMapping("/user/{name}/{password}")
	public UserData getUserByName(@PathVariable String name, @PathVariable String password) {
		
		log.info("Getting User With Name: {}", name);
		
		return pS.getUserWithNamePassword(name, password);
		
	}
	
	// Updates User's Password Using The User's Name & Old Password
	@PutMapping("/user/{name}/{oldPassword}/{newPassword}")
	public UserData updateUserPassword(@PathVariable String name, @PathVariable String oldPassword, @PathVariable String newPassword) {
		
		log.info("Updating Password To: {}", newPassword);
		
		// Finds The User
		UserData userd = getUserByName(name, oldPassword);
		
		return pS.saveUserPassword(userd.toUser(), newPassword);
		
	}
	
	// Deletes An User Using Name & Password
		@DeleteMapping("/user/{name}/{password}")
		public Map<String, String> deleteUserUsingName(@PathVariable String name, @PathVariable String password) {
			
			// Finds The User
			UserData ud = getUserByName(name, password);
			
			log.info("Deleting User: {}", ud);
			
			return Map.of("message", "Deletion of User With Name: " + name + " Completed: " +
				pS.deleteUserWithPassword(ud)); // Returns A Boolean Identifying if Deleted
			
		}
	
	// Adds An User To a Project
	@PutMapping("/user/add/{name}/{password}/{projectId}")
	public UserData addProjectToUser(@PathVariable String name, @PathVariable String password,
			@PathVariable Long projectId) {
		
		return pS.addUserToProject(getUserByName(name, password), projectId);
		
	}
	
	// Removes An User From a Project
	@DeleteMapping("/user/{name}/{password}/{projectId}")
	public Map<String, String> removeProjectFromUser(@PathVariable String name, @PathVariable String password,
			@PathVariable Long projectId) {
		
		log.info("Removing Project With ID: {}", projectId);
		
		return Map.of("message", "Removal of Project With Id: " + projectId + " Completed: " + 
			pS.removeUserFromProject(getUserByName(name, password), projectId)); // Returns A Boolean Identifying if Deleted
		
	}
	
	// Creates A Project Using An User's ID
	@PostMapping("/user/project/{userid}")
	public ProjectData createProject(@RequestBody ProjectData projectData, @PathVariable Long userid) {
		
		log.info("Creating Project: {}", projectData);
		
		return pS.createProject(projectData, userid);
		
	}
	
	// Gets All Users
	@GetMapping("/user/project")
	public List<ProjectData> getAllProjects() {
		
		log.info("Getting All Projects");
		
		return pS.getAllProjects(true);
		
	}
	
	// Updates A Project Using It's ID
	@PutMapping("/user/project/{id}")
	public ProjectData updateProjectWithId(@RequestBody ProjectData projectData, @PathVariable Long id) {
		
		log.info("Updating Project With Id: " + id);
		
		return pS.updateProject(projectData, id);
		
	}
	
	// Deletes A Project Using It's ID
	@DeleteMapping("/user/project/{id}")
	public Map<String, String> deleteProjectWithID(@PathVariable Long id) {
		
		log.info("Deleting Project With Id:{}", id);
		
		return Map.of("message", "Deletion of Project With Id: " + id + " Completed: " +
			pS.deleteProjectWithId(id)); // Returns A Boolean Identifying if Deleted
		
	}
	
	// Creates A Step Using A Project's ID
	@PostMapping("/user/project/step/{projectId}")
	public StepData createStep(@RequestBody StepData stepData, @PathVariable Long projectId) {
		
		log.info("Creating Step: {}", stepData);
		
		return pS.createStep(stepData, projectId);
		
	}
	
	// Updates A Step Using It's ID & The Project's ID
	@PutMapping("/user/project/step/{stepId}/{projectId}")
	public StepData updateStep(@RequestBody StepData stepData, @PathVariable Long stepId, @PathVariable Long projectId) {
		
		log.info("Updating Step With Id: {}", stepId);
		
		// Sets The ID So It Can Be Saved Correctly
		stepData.setStepId(stepId);
		
		return pS.updateStep(stepData);
		
	}
	
	// Deletes A Step Using It's ID & The Project's ID
	@DeleteMapping("/user/project/step/{stepId}/{projectId}")
	public Map<String, String> deleteStep(@PathVariable Long stepId, @PathVariable Long projectId) {
		
		log.info("Deleting Step With Id: {}", stepId);
		
		return Map.of("message", "Deletion of Step With Id: " + stepId + " Completed: " +
				pS.deleteStep(stepId, projectId)); // Returns A Boolean Identifying if Deleted
		
	}
	
}
