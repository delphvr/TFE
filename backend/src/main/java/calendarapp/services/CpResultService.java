package calendarapp.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.CpResult;
import calendarapp.repository.CpResultRepository;

@Service
public class CpResultService {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private RehearsalService rehearsalService;
    @Autowired
    private CpResultRepository cpResultRepository;

    /**
     * Save the give cp result in the database or updates it
     * 
     * @param cp_result the result to be save in the database
     * @return the saved result
     * @throws IllegalArgumentException if no project is found with the given
     *                                  project id,
     *                                  or if no rehearsal is found with the given
     *                                  rehearsal id
     */
    public CpResult createCp(CpResult cp_result) {
        projectService.isProject(cp_result.getProjectId());
        rehearsalService.isRehearsal(cp_result.getRehearsalId());
        CpResult res = cpResultRepository.save(cp_result);
        return res;
    }

    /**
     * Update the date of the accepeted rehearsal and return the list of rehearsal with the date that was not accepeted.
     * 
     * @param projectId the id of the project
     * @return Map rehearsal id : localDateTime not accepeted
     * @throws IllegalArgumentException if no project is found with the given
     *                                  project id
     */
    public Map<Long, LocalDateTime> checkPreviousResult(Long projectId){
        Map<Long, LocalDateTime> res = new HashMap<>();
        projectService.isProject(projectId);
        List<CpResult> cpResults = cpResultRepository.findByProjectId(projectId);
        for(CpResult cpResult: cpResults){
            if(cpResult.isAccepted()){
                rehearsalService.updateReheasalDateAndTime(cpResult.getRehearsalId(), projectId, cpResult.getBeginningDate());
            }
            else {
                rehearsalService.updateReheasalDateAndTime(cpResult.getRehearsalId(), projectId, null);
                //TODO in the db? so can recompute several times and remeber all the previous negation
                res.put(cpResult.getRehearsalId(), cpResult.getBeginningDate());
            }
        }
        return res;
    }

}
