package calendarapp.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.Project;
import calendarapp.model.Rehearsal;
import calendarapp.repository.ProjectRepository;
import calendarapp.repository.RehearsalRepository;

@Service
public class RehearsalService {

    @Autowired
    private RehearsalRepository rehearsalRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Return the list of rehersal in the project with id ´projectId´
     * 
     * @param projectId the id of the project for wich we want to retreive it's
     *                  rehearsal
     * @return the list of rehearsal associated with the project with project id
     *         ´projectId´
     *         sort by date.
     */
    public List<Rehearsal> getProjectRehearsals(Long projectId) {
        projectService.isProject(projectId);
        List<Rehearsal> rehearsals = rehearsalRepository.findByProjectId(projectId);
        rehearsals.sort(Comparator.comparing(Rehearsal::getDate));
        return rehearsals;
    }

    /**
     * Add the ´rehearsal´ to the database
     * 
     * @param rehearsal the rehearsal to save in the database
     * @throws IllegalArgumentException if no project is found for the given project
     *                                  id
     *                                  or if the date of the rehearsal is in the
     *                                  past
     * @return the newly added rehearsal
     */
    public Rehearsal createRehearsal(Rehearsal rehearsal) {
        projectService.isProject(rehearsal.getProjectId());
        LocalDate now = LocalDate.now();
        if (rehearsal.getDate().isBefore(now)) {
            throw new IllegalArgumentException("The rehearsal date cannot be in the past");
        }
        Optional<Project> project = projectRepository.findById(rehearsal.getProjectId());
        if (project.get().getEndingDate() != null && rehearsal.getDate().isAfter(project.get().getEndingDate())) {
            throw new IllegalArgumentException("The rehearsal date cannot be after the project has ended");
        }
        if (project.get().getBeginningDate() != null
                && rehearsal.getDate().isBefore(project.get().getBeginningDate())) {
            throw new IllegalArgumentException("The rehearsal date cannot be before the project has started");
        }
        Rehearsal res = rehearsalRepository.save(rehearsal);
        return res;
    }

}
