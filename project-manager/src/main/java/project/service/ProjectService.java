package project.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.controller.error.DuplicateEntryException;
import project.controller.model.ProjectData;
import project.controller.model.StepData;
import project.controller.model.UserData;
import project.dao.ProjectDao;
import project.dao.StepDao;
import project.dao.UserDao;
import project.entity.Project;
import project.entity.User;

@Service
public class ProjectService {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private StepDao stepDao;
	
	// Finds A Project Using An ID
	@Transactional(readOnly = true)
	public ProjectData findProjectById(Long projectId) {
		return new ProjectData(projectDao.findById(projectId).orElseThrow(() -> 
		new NoSuchElementException("Project With Id: " + projectId + " Does Not Exist")));
	}
	
	// Creates An User Using An User DTO
	@Transactional(readOnly = false)
	public UserData createUser(UserData userData) {
		
		// A Boolean To Track If An User With The Same Name Exists
		boolean conflict = false;
		
		for(UserData user : getAllUsers(false)) { // Checks If Any User Has The Same Name Regardless Of Capitalization
			if(user.getName().toLowerCase().equals(userData.getName().toLowerCase())) {
				conflict = true;
			}
		}
		
		if(!conflict) { // If No Conflict It Saves The User
			
			return new UserData(userDao.save(userData.toUser()), true);
			
		} else { // Throws An Error If There Already Is An User With The Same Name
			
			throw new DuplicateEntryException("User With Name: " + userData.getName() + " Already Exists!");
			
		}
	}
	
	// Gets All Users
	@Transactional(readOnly = true)
	public List<UserData> getAllUsers(boolean blur) {
		
		List<UserData> results = new LinkedList<>();
		
		if(blur) { // Uses A Temporary User To Blur Out Passwords And Saved To A Different List
		
			for(User u : userDao.findAll()) {
			
				User temp = new User();
				temp.setName(u.getName());
				temp.setUserId(u.getUserId());
				temp.setProjects(u.getProjects());
				temp.setPassword(null);
			
				results.add(new UserData(temp, true));
			
			}
		
		} else { // If Passwords Are Needed
			
			for(User u : userDao.findAll()) {
				results.add(new UserData(u, true));
			}
			
		}
		
		return results;
		
	}
	
	// Gets An User Using The Name & Password
	@Transactional(readOnly = true)
	public UserData getUserWithNamePassword(String name, String password) {
		
		for(UserData u : getAllUsers(false)) {
			
			if(u.getPassword().equals(password) && u.getName().equals(name)) { // Checks If Name And Password Match
				
				return u;
				
			}
			
		}
		
		// Throws An Exception If The User Isn't Found
		throw new NoSuchElementException("User With Password: " + password + " Not Found!");
	}
	
	// Finds User By ID
	@Transactional(readOnly = true)
	public UserData getUserWithId(Long id) {
		
		return new UserData(userDao.findById(id).orElseThrow( () -> new NoSuchElementException("Cannot Find User With ID: " + id) ), true);
		
	}
	
	// Saves & Updates User's New Password
	@Transactional(readOnly = false)
	public UserData saveUserPassword(User user, String newPassword) {
		
		user.setPassword(newPassword);
		
		return new UserData(userDao.save(user), true);
	}
	
	// Deletes A User After Finding It With A Password
	@Transactional(readOnly = false)
	public boolean deleteUserWithPassword(UserData u) {
		
		try { // If Object Exists It Wont Throw An Error
			userDao.delete(u.toUser());
			return true;
			
		} catch(Exception e) {
			
			return false;
			
		}
		
	}
	
	// Updates An User
	@Transactional(readOnly = false)
	public void updateUser(UserData ud) {
		
		userDao.save(ud.toUser());
		
	}
	
	// Unlinks An User And Project
	@Transactional(readOnly = false)
	public boolean removeUserFromProject(UserData userData, Long projectId) {
		
		ProjectData pd = findProjectById(projectId); // Gets The Parent Project
		boolean removed = false;
		
		for(UserData u : pd.getUsers()) { // Goes Through All Users & If User Name & Password Match Removes That User From The Set
			
			if(u.getName().equals(userData.getName()) && u.getPassword().equals(userData.getPassword())) {
				u.getProjects().remove(pd);
				removed = pd.getUsers().remove(u); // Confirm If Actually Found And Removed
			}
			
		}
		
		updateProject(pd, projectId);
		
		return removed;
		
	}
	
	// Links An User And Project Together
	@Transactional(readOnly = false)
	public UserData addUserToProject(UserData userData, Long projectId) {
		
		ProjectData pd = findProjectById(projectId);
		
		pd.getUsers().add(userData);
		
		pd = updateProject(pd, projectId); // Updates The ProjectData Object To New Version
		
		// Goes Through And Tries To Find The User Just Added
		for(UserData u : pd.getUsers()) {if(u.getUserId().equals(userData.getUserId())) { return u; }}
		
		// Otherwise Failed
		return null;
	}
	
	// Creates A Project
	@Transactional(readOnly = false)
	public ProjectData createProject(ProjectData projectData, Long userid) {
		
		Project p = projectData.toProject();
		p.getUsers().add( getUserWithId(userid).toUser() ); // Finds The Correct User To Add
		
		return new ProjectData(projectDao.save(p));
	}
	
	// Gets All Projects
	@Transactional(readOnly = true)
	public List<ProjectData> getAllProjects(Boolean blur) {
		
		List<ProjectData> results = new LinkedList<>();
		
		for(Project p : projectDao.findAll()) {
			
			if(blur) { // Sets All User Passwords To Null
				Set<User> users = new HashSet<>();
				for(User u : p.getUsers()) { User t = u; t.setPassword(null); users.add(t); }
				
				Project tempProject = p;
				tempProject.setUsers(users);
				
				results.add(new ProjectData(tempProject));
				
			} else { // Sends All User Information Including Passwords
				results.add(new ProjectData(p));
			} 
			
		}
		
		return results;
	}
	
	// Updates A Project
	@Transactional(readOnly = false)
	public ProjectData updateProject(ProjectData projectData, Long id) {
		
		if(Objects.isNull(projectData.getProjectId())) { // Overrides User & Step Lists
			Project old = findProjectById(id).toProject();
			
			Set<UserData> users = new ProjectData(old).getUsers();
			Set<StepData> steps = new ProjectData(old).getSteps();
			
			projectData.setUsers(users);
			projectData.setSteps(steps);
		}
		
		projectData.setProjectId(id);
		
		return new ProjectData(projectDao.save(projectData.toProject()));
	}
	
	// Deletes A Project Using An ID
	@Transactional(readOnly = false)
	public boolean deleteProjectWithId(Long id) {
		try {
			
			projectDao.delete(findProjectById(id).toProject()); // Finds Project With ID Then Deletes It
			return true;
			
		} catch (NoSuchElementException e) { // Fails If Project Does Not Exist
			
			return false;
			
		}
		
	}

	// Creates A Step
	@Transactional(readOnly = false)
	public StepData createStep(StepData stepData, Long projectId) {
		
		ProjectData p = findProjectById(projectId);
			
		p.getSteps().add(stepData);
			
		p = new ProjectData(projectDao.save(p.toProject())); // Overrides The Project To The Updated One
		
		for(StepData s : p.getSteps()) { // Finds The Newly Updated Step That Has An ID
			if(s.getStepOrder().equals(stepData.getStepOrder()) && s.getStepText().equals(stepData.getStepText())) {
				stepData = s;
			}
		}
		
		return stepData;
	}

	// Updates A Step
	@Transactional(readOnly = false)
	public StepData updateStep(StepData stepData) { return new StepData(stepDao.save(stepData.toStep())); }
	
	// Deletes A Step
	@Transactional(readOnly = false)
	public boolean deleteStep(Long stepId, Long projectId) {

		ProjectData p = findProjectById(projectId); // Finds Parent Project
		boolean removed = false;
		
		for(StepData s : p.getSteps()) { // Goes Through The Steps In The Project & Then Removes It
			
			if(s.getStepId().equals(stepId)) {
				p.getSteps().remove(s);
				removed = true;
			}
			
		}
		
		updateProject(p, projectId); // Then Updates It
		
		return removed;
		
	}


}
