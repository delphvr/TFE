package calendarapp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.Rehearsal;
import calendarapp.model.RehearsalPrecedence;
import calendarapp.model.RehearsalPrecedenceId;
import calendarapp.repository.RehearsalPrecedenceRepository;
import calendarapp.response.RehearsalPrecedenceResponse;
import calendarapp.response.RehearsalResponse;
import jakarta.transaction.Transactional;


@Service
public class RehearsalPrecedenceService {

    @Autowired
    private RehearsalPrecedenceRepository rehearsalPrecedenceRepository;
    @Autowired
    private RehearsalService rehearsalService;


    /**
     * Save a list of rehearsals precedences to the database.
     * 
     * @param rehearsalId the current rehearsal id
     * @param precedingRehearsalsId the list of rehearsals id that representes all the rehearsal that preceeds the current rehearsal.
     * @return the list of saved rehearsals precedences
     * @throws IllegalArgumentException if no rehearsal is found with one of the given ids,
     *                                  or trying to add a precedence relation with itself,
     *                                  or there alreday existe an opposite rehearsals precedences
     */
    public List<RehearsalPrecedence> createRehearsalPrecedences(Long rehearsalId,  List<Long> precedingRehearsalsId){
        rehearsalService.isRehearsal(rehearsalId);
        List<RehearsalPrecedence> res = new ArrayList<>();
        for (Long precedingRehearsalId : precedingRehearsalsId){
            if (precedingRehearsalId == rehearsalId){
                throw new IllegalArgumentException("Cannot add a precedence relation with itself");
            }
            if (rehearsalPrecedenceRepository.existsById(new RehearsalPrecedenceId(precedingRehearsalId, rehearsalId))){
                throw new IllegalArgumentException("Cannot add contradictory precedences. Rehearsal " + rehearsalId + "already preceeds rehearsal " + precedingRehearsalId);
            }
            RehearsalPrecedence rehearsalPrecedence = new RehearsalPrecedence(rehearsalId, precedingRehearsalId);
            res.add(rehearsalPrecedenceRepository.save(rehearsalPrecedence));
        }
        return res;
    }

    /**
     * Get the rehearsal relation with the other rehearsal on the project.
     * 
     * @param rehearsalId the id of the rehearsal
     * @return the list of preceding, following rehearsals id and rehearsals with no relation.
     * @throws IllegalArgumentException if no rehearsal is found with the id `rehearsalId`,
     */
    public RehearsalPrecedenceResponse getRehersalsPrecedences(Long rehearsalId){
        //TODO R1 before R2 and R3 before R1 and R2 before R3 cannot do how to check ?
        Rehearsal currentRehearsal = rehearsalService.getRehearsal(rehearsalId);
        Long projectId = currentRehearsal.getProjectId();
        List<Rehearsal> previous = new ArrayList<>();
        List<Rehearsal> following = new ArrayList<>();
        List<Rehearsal> notConstraint = new ArrayList<>();
        for(RehearsalResponse rehearsal : rehearsalService.getProjectRehearsals(projectId)){
            if (rehearsalPrecedenceRepository.existsById(new RehearsalPrecedenceId(rehearsalId, rehearsal.getId()))){
                previous.add(new Rehearsal(rehearsal.getName(), rehearsal.getDescription(), rehearsal.getDate(), rehearsal.getTime(), rehearsal.getDuration(), rehearsal.getProjectId(), rehearsal.getLocation()));
            } else if(rehearsalPrecedenceRepository.existsById(new RehearsalPrecedenceId(rehearsal.getId(), rehearsalId))){
                following.add(new Rehearsal(rehearsal.getName(), rehearsal.getDescription(), rehearsal.getDate(), rehearsal.getTime(), rehearsal.getDuration(), rehearsal.getProjectId(), rehearsal.getLocation()));
            } else {
                notConstraint.add(new Rehearsal(rehearsal.getName(), rehearsal.getDescription(), rehearsal.getDate(), rehearsal.getTime(), rehearsal.getDuration(), rehearsal.getProjectId(), rehearsal.getLocation()));
            }
        }
        return new RehearsalPrecedenceResponse(previous, following, notConstraint);
    }

    @Transactional
    public void deleteRehearsalPrecedence(RehearsalPrecedence rehearsalPrecedence){
        rehearsalPrecedenceRepository.delete(rehearsalPrecedence);
    }
    
}
