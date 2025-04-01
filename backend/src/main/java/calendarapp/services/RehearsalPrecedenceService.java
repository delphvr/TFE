package calendarapp.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
     * @param rehearsalId           the current rehearsal id
     * @param precedingRehearsalsId the list of rehearsals id that representes all
     *                              the rehearsal that preceeds the current
     *                              rehearsal.
     * @return the list of saved rehearsals precedences
     * @throws IllegalArgumentException if no rehearsal is found with one of the
     *                                  given ids,
     *                                  or trying to add a precedence relation with
     *                                  itself,
     *                                  or there alreday existe an opposite
     *                                  rehearsals precedences (e.g a->b->a)
     */
    public List<RehearsalPrecedence> createRehearsalPrecedences(Long rehearsalId, List<Long> precedingRehearsalsId) {
        rehearsalService.isRehearsal(rehearsalId);
        List<RehearsalPrecedence> res = new ArrayList<>();
        for (Long precedingRehearsalId : precedingRehearsalsId) {
            if (precedingRehearsalId == rehearsalId) {
                throw new IllegalArgumentException("Cannot add a precedence relation with itself");
            }
            if (!isPossible(rehearsalId, precedingRehearsalId)) {
                throw new IllegalArgumentException("Cannot add contradictory precedences.");
            }
            RehearsalPrecedence rehearsalPrecedence = new RehearsalPrecedence(rehearsalId, precedingRehearsalId);
            res.add(rehearsalPrecedenceRepository.save(rehearsalPrecedence));
        }
        return res;
    }

    /**
     * Check if it is possible to a the rehearsal precedence `preceding`->`current`
     * without creating inconstsistency in the global rehearsals precedences.
     * 
     * @param current   the rehearsal id of the Reheasal
     * @param preceding the rehearsal id of the rehearsal that has to preced
     *                  ´current´
     * @return true if adding this rehearsal precedence isn't inconsistente with the
     *         rest, false otherwise
     */
    boolean isPossible(Long current, Long preceding) {
        // Check that it is impossible to go from preceding to current
        Stack<Long> exploring = new Stack<>();
        Set<Long> explored = new HashSet<>();
        exploring.add(preceding);
        while (!exploring.isEmpty()) {
            Long node = exploring.pop();
            if (node.equals(current)) {
                return false;
            }
            if (explored.contains(node)) {
                continue;
            }
            explored.add(node);
            List<RehearsalPrecedence> neighbors = rehearsalPrecedenceRepository.findByCurrent(node);
            for (RehearsalPrecedence neighbor : neighbors) {
                exploring.add(neighbor.getPrevious());
            }
        }
        return true;
    }

    /**
     * Get the rehearsal relation with the other rehearsal on the project.
     * 
     * @param rehearsalId the id of the rehearsal
     * @return the list of preceding, following rehearsals id and rehearsals with no
     *         relation.
     * @throws IllegalArgumentException if no rehearsal is found with the id
     *                                  `rehearsalId`,
     */
    public RehearsalPrecedenceResponse getRehersalsPrecedences(Long rehearsalId) {
        Rehearsal currentRehearsal = rehearsalService.getRehearsal(rehearsalId);
        Long projectId = currentRehearsal.getProjectId();
        List<Rehearsal> previous = new ArrayList<>();
        List<Rehearsal> following = new ArrayList<>();
        List<Rehearsal> notConstraint = new ArrayList<>();
        for (RehearsalResponse rehearsal : rehearsalService.getProjectRehearsals(projectId)) {
            if (rehearsalPrecedenceRepository.existsById(new RehearsalPrecedenceId(rehearsalId, rehearsal.getId()))) {
                previous.add(new Rehearsal(rehearsal.getName(), rehearsal.getDescription(), rehearsal.getDate(),
                        rehearsal.getTime(), rehearsal.getDuration(), rehearsal.getProjectId(),
                        rehearsal.getLocation()));
            } else if (rehearsalPrecedenceRepository
                    .existsById(new RehearsalPrecedenceId(rehearsal.getId(), rehearsalId))) {
                following.add(new Rehearsal(rehearsal.getName(), rehearsal.getDescription(), rehearsal.getDate(),
                        rehearsal.getTime(), rehearsal.getDuration(), rehearsal.getProjectId(),
                        rehearsal.getLocation()));
            } else {
                notConstraint.add(new Rehearsal(rehearsal.getName(), rehearsal.getDescription(), rehearsal.getDate(),
                        rehearsal.getTime(), rehearsal.getDuration(), rehearsal.getProjectId(),
                        rehearsal.getLocation()));
            }
        }
        return new RehearsalPrecedenceResponse(previous, following, notConstraint);
    }

    @Transactional
    public void deleteRehearsalPrecedence(RehearsalPrecedence rehearsalPrecedence) {
        rehearsalPrecedenceRepository.delete(rehearsalPrecedence);
    }

}
