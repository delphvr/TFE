package calendarapp.services;

import calendarapp.model.Project;
import calendarapp.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<Project>();
        projectRepository.findAll().forEach(projects::add);
        if (projects.isEmpty()) {
            throw new NoSuchElementException("No projects found in the database.");
        }
        return projects;
    }

    public Project createProject(Project request) {
        Project project = new Project(null, request.getName(), request.getDescription(), request.getBeginningDate(),
                request.getEndingDate());
        project = projectRepository.save(project);
        return project;
    }

    public Project updateProject(long id, Project project) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()) {
            Project _project = projectData.get();
            _project.setName(project.getName());
            _project.setDescription(project.getDescription());
            _project.setBeginningDate(project.getBeginningDate());
            _project.setEndingDate(project.getEndingDate());
            return projectRepository.save(_project);
        } else {
            throw new IllegalArgumentException("Project not found with id " + id);
        }
    }

    public void deleteProject(long id) {
        projectRepository.deleteById(id);
    }

    public void deleteAllProjects() {
        projectRepository.deleteAll();
    }

}
