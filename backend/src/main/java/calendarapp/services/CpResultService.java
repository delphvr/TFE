package calendarapp.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.CpResult;
import calendarapp.model.CpResultId;
import calendarapp.model.RehearsalPresence;
import calendarapp.model.User;
import calendarapp.repository.CpResultRepository;
import jakarta.transaction.Transactional;

@Service
public class CpResultService {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private RehearsalService rehearsalService;
    @Autowired
    private CpPresenceResultService cpPresenceResultService;
    @Autowired
    private CpResultRepository cpResultRepository;

    /**
     * Save the give cp result in the database or updates it
     * 
     * @param cpResult the result to be save in the database
     * @return the saved result
     * @throws IllegalArgumentException if no project is found with the given
     *                                  project id,
     *                                  or if no rehearsal is found with the given
     *                                  rehearsal id
     */
    public CpResult createCp(CpResult cpResult) {
        projectService.isProject(cpResult.getProjectId());
        rehearsalService.isRehearsal(cpResult.getRehearsalId());
        CpResult res = cpResultRepository.save(cpResult);
        return res;
    }

    /**
     * Update the date of the accepeted rehearsal and return the list of rehearsal
     * with the date that was not accepeted.
     * 
     * @param projectId the id of the project
     * @return Map rehearsal id : localDateTime not accepeted
     * @throws IllegalArgumentException if no project is found with the given
     *                                  project id
     */
    public Map<Long, LocalDateTime> checkPreviousResult(Long projectId) {
        Map<Long, LocalDateTime> res = new HashMap<>();
        projectService.isProject(projectId);
        List<CpResult> cpResults = cpResultRepository.findByProjectId(projectId);
        for (CpResult cpResult : cpResults) {
            if (cpResult.isAccepted()) {
                rehearsalService.updateReheasalDateAndTime(cpResult.getRehearsalId(), projectId,
                        cpResult.getBeginningDate());
            } else {
                rehearsalService.updateReheasalDateAndTime(cpResult.getRehearsalId(), projectId, null);
                // TODO in the db? so can recompute several times and remeber all the previous
                // negation
                res.put(cpResult.getRehearsalId(), cpResult.getBeginningDate());
            }
        }
        return res;
    }

    /**
     * Change the accepted status of the cp result with project id `projectid` and
     * rehearsal id `rehearsalId` to `accepted`.
     * 
     * @param projectId   the id of the project
     * @param rehearsalId the id of the rehearsal
     * @param accepted    the new accepeted status
     * @return the updated cp result
     * @throws IllegalArgumentException if no project found with id `projectId`,
     *                                  or no rehearsal found with id `rehearsalId`,
     *                                  or no cp result found for the given
     *                                  rehearsal in the given project
     */
    public CpResult setIsAccepted(Long projectId, Long rehearsalId, boolean accepted) {
        projectService.isProject(projectId);
        rehearsalService.isRehearsal(rehearsalId);
        Optional<CpResult> cpResult = cpResultRepository.findById(new CpResultId(projectId, rehearsalId));
        if (!cpResult.isPresent()) {
            throw new IllegalArgumentException(
                    "Cp result not found with project id " + projectId + " and rehearsal id " + rehearsalId);
        }
        CpResult res = cpResult.get();
        res.setAccepted(accepted);
        res = cpResultRepository.save(res);
        return res;
    }

    /**
     * Accepted all cp result for the project with id `projectId`.
     * Updates the rehearsals date and delete the cp result from the database.
     * Put/update the presence of the users at the rehearsal.
     * 
     * @param projectId the id of the project
     * @throws IllegalArgumentException if no project found with id `projectId`
     */
    @Transactional
    public void acceptAll(Long projectId) {
        projectService.isProject(projectId);
        List<CpResult> cpResults = cpResultRepository.findByProjectId(projectId);
        for (CpResult cpResult : cpResults) {
            List<User> users = rehearsalService.getRehearsalParticipants(cpResult.getRehearsalId());
            for (User user : users) {
                RehearsalPresence rehearsalPresence = new RehearsalPresence(cpResult.getRehearsalId(), user.getId(),
                        cpPresenceResultService.getIsPresent(cpResult.getRehearsalId(), user.getId()));
                rehearsalService.createOrUpdateRehearsalPresence(rehearsalPresence);
            }
            rehearsalService.updateReheasalDateAndTime(cpResult.getRehearsalId(), projectId,
                    cpResult.getBeginningDate());
            cpResultRepository.delete(cpResult);
        }
    }

}
