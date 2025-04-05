package calendarapp.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.CpPresenceResult;
import calendarapp.model.CpPresenceResultId;
import calendarapp.repository.CpPresenceResultRepository;
import calendarapp.response.RehearsalResponse;

@Service
public class CpPresenceResultService {
    @Autowired
    private UserService userService;
    @Autowired
    private RehearsalService rehearsalService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private CpPresenceResultRepository cpPresenceResultRepository;

    /**
     * Save the give cp presence result in the database or updates it
     * 
     * @param cpPresenceResult the presence result to be save in the database
     * @return the saved result
     * @throws IllegalArgumentException if no user is found with the given
     *                                  user id,
     *                                  or if no rehearsal is found with the given
     *                                  rehearsal id
     */
    public CpPresenceResult createCpPresence(CpPresenceResult cpPresenceResult) {
        userService.isUser(cpPresenceResult.getUserId());
        rehearsalService.isRehearsal(cpPresenceResult.getRehearsalId());
        CpPresenceResult res = cpPresenceResultRepository.save(cpPresenceResult);
        return res;
    }

    /**
     * Get for each rehearsals of the project who can attempte the rehearsal and who
     * can't for the cp proposition.
     * 
     * @param projectId the id of the project
     * @return a Map with as key the rehearsals id, and as values another map with
     *         as key the user id and as value a boolean representing if the user is
     *         present at the rehearsal or not
     * @throws IllegalArgumentException if no project is found with the given id
     */
    public Map<Long, Map<Long, Boolean>> getCpPresences(Long projectId) {
        projectService.isProject(projectId);
        List<RehearsalResponse> rehearsals = rehearsalService.getProjectRehearsals(projectId);
        Map<Long, Map<Long, Boolean>> res = new HashMap<>();
        for (RehearsalResponse rehearsal : rehearsals) {
            Map<Long, Boolean> rehearsalsParticipation = new HashMap<>();
            for (Long userId : rehearsal.getParticipantsIds()) {
                Optional<CpPresenceResult> presence = cpPresenceResultRepository
                        .findById(new CpPresenceResultId(rehearsal.getId(), userId));
                rehearsalsParticipation.put(userId, presence.get().isPresent());
            }
            res.put(rehearsal.getId(), rehearsalsParticipation);
        }
        return res;
    }

    /**
     * Get is a user present at a given rehearsal in the sugestion from the cp.
     * 
     * @param rehearsalId the id of the rehearsal
     * @param userId the id of the user
     * @return is the user suppose to be present at the rehearsal
     * @throws IllegalArgumentException if no rehearsal is found with the given id,
     *                                  or if no user is found with the given id
     */
    public boolean getIsPresent(Long rehearsalId, Long userId){
        rehearsalService.isRehearsal(rehearsalId);
        userService.isUser(userId);
        Optional<CpPresenceResult> presence = cpPresenceResultRepository
                        .findById(new CpPresenceResultId(rehearsalId, userId));
        return presence.get().isPresent();
    }

}
