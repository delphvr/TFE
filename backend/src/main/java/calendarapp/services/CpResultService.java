package calendarapp.services;

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
    private CpResultRepository cpRepository;

    /**
     * Save the give cp result in the database
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
        CpResult res = cpRepository.save(cp_result);
        return res;
    }

}
